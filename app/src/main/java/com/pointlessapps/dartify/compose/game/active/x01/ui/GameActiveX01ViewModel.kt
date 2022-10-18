package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.mappers.*
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.addDecimal
import com.pointlessapps.dartify.domain.game.x01.score.usecase.IsCheckoutPossibleUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateScoreUseCase
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import com.pointlessapps.dartify.domain.game.x01.turn.model.WinState
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameActiveX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameActiveX01Event
    object NavigateBack : GameActiveX01Event

    data class AskForNumberOfThrowsAndDoubles(
        val minNumberOfThrows: Int,
        val maxNumberOfDoubles: Map<Int, Int>,
    ) : GameActiveX01Event

    @JvmInline
    value class AskForNumberOfThrows(val minNumberOfThrows: Int) : GameActiveX01Event

    @JvmInline
    value class AskForNumberOfDoubles(val maxNumberOfDoubles: Int) : GameActiveX01Event

    data class ShowWinnerDialog(val playerScore: PlayerScore) : GameActiveX01Event
}

internal data class GameActiveX01State(
    val currentSet: Int = 1,
    val currentLeg: Int = 1,
    val playersScores: List<PlayerScore> = emptyList(),
    val currentPlayer: Player? = null,
    val currentInputScore: Int = 0,
)

internal class GameActiveX01ViewModel(
    private val validateScoreUseCase: ValidateScoreUseCase,
    private val isCheckoutPossibleUseCase: IsCheckoutPossibleUseCase,
    private val nextTurnUseCase: NextTurnUseCase,
    private val doneTurnUseCase: DoneTurnUseCase,
    private val addInputUseCase: AddInputUseCase,
    private val undoTurnUseCase: UndoTurnUseCase,
    private val finishLegUseCase: FinishLegUseCase,
    private val setupGameUseCase: SetupGameUseCase,
) : ViewModel() {

    private lateinit var gameSettings: GameSettings

    var state by mutableStateOf(GameActiveX01State())
        private set

    private val eventChannel = Channel<GameActiveX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun setGameSettings(gameSettings: GameSettings) {
        this.gameSettings = gameSettings
        val currentState = setupGameUseCase(
            gameSettings.players.map(Player::toPlayer),
            gameSettings.startingScore,
            gameSettings.inMode.toInMode(),
            gameSettings.numberOfSets,
            gameSettings.numberOfLegs,
            gameSettings.matchResolutionStrategy.toMatchResolutionStrategy(),
        )

        state = state.copy(
            currentInputScore = currentState.score,
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    fun onPossibleCheckoutRequested(): Int? {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return null

        if (
            isCheckoutPossibleUseCase(
                currentPlayerScore.scoreLeft,
                outMode = currentPlayerScore.player.outMode.toOutMode(),
            )
        ) {
            return currentPlayerScore.scoreLeft
        }

        return null
    }

    fun onScoreLeftRequested(player: Player): Int {
        val playerScore = state.playersScores.find {
            it.player == player
        } ?: return 0

        return playerScore.scoreLeft - if (player.id == state.currentPlayer?.id) {
            state.currentInputScore
        } else {
            0
        }
    }

    fun onQuickScoreClicked(quickScore: Int) {
        if (!validateScoreUseCase(quickScore)) {
            return
        }

        state = state.copy(
            currentInputScore = quickScore,
        )

        onDoneClicked()
    }

    fun onKeyClicked(key: Int) {
        if (!validateKey(key)) {
            return
        }

        state = state.copy(
            currentInputScore = state.currentInputScore.addDecimal(key),
        )
    }

    fun onUndoClicked() {
        if (state.currentInputScore != 0) {
            state = state.copy(
                currentInputScore = 0,
            )

            return
        }

        val currentState = undoTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score,
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    fun onDoneClicked() {
        if (!validateScoreUseCase(state.currentInputScore)) {
            // TODO show error snackbar
            return
        }

        viewModelScope.launch {
            when (val event = doneTurnUseCase(state.currentInputScore)) {
                is DoneTurnEvent.AskForNumberOfDoubles -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfDoubles(
                        maxNumberOfDoubles = event.maxNumberOfDoublesForThreeThrows,
                    ),
                )
                is DoneTurnEvent.AskForNumberOfThrows -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfThrows(event.minNumberOfThrows),
                )
                is DoneTurnEvent.AskForNumberOfThrowsAndDoubles -> eventChannel.send(
                    GameActiveX01Event.AskForNumberOfThrowsAndDoubles(
                        minNumberOfThrows = event.minNumberOfThrows,
                        maxNumberOfDoubles = event.maxNumberOfDoubles,
                    ),
                )
                is DoneTurnEvent.AddInput -> {
                    addInputUseCase(state.currentInputScore, numberOfThrowsOnDouble = 0)
                    val currentState = nextTurnUseCase()
                    state = state.copy(
                        currentInputScore = currentState.score,
                        currentSet = currentState.set,
                        currentLeg = currentState.leg,
                        currentPlayer = currentState.player.fromPlayer(),
                        playersScores = currentState.playerScores.map { it.fromPlayerScore() },
                    )
                }
            }
        }
    }

    fun onClearClicked() {
        state = state.copy(
            currentInputScore = 0,
        )
    }

    fun onNumberOfDoublesClicked(numberOfDoubles: Int) {
        addInputUseCase(state.currentInputScore, numberOfThrowsOnDouble = numberOfDoubles)
        val currentState = nextTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score,
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfThrowsOnDoubles: Int = 0) {
        when (val state = finishLegUseCase(numberOfThrows, numberOfThrowsOnDoubles)) {
            is WinState -> {
                viewModelScope.launch {
                    eventChannel.send(
                        GameActiveX01Event.ShowWinnerDialog(
                            state.playerScore.fromPlayerScore(),
                        ),
                    )
                }
            }
            is CurrentState -> {
                this.state = this.state.copy(
                    currentInputScore = state.score,
                    currentSet = state.set,
                    currentLeg = state.leg,
                    currentPlayer = state.player.fromPlayer(),
                    playersScores = state.playerScores.map { it.fromPlayerScore() },
                )
            }
        }
    }

    fun getCurrentFinishSuggestion(): String? {
        val playerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return null

        // TODO add actual implementation of this
        if (playerScore.scoreLeft == 100) {
            return "T20 D20"
        }

        return null
    }

    fun onShowGameStatsClicked() {
        // TODO do something
    }

    fun onSaveAndCloseClicked() {
        viewModelScope.launch {
            eventChannel.send(GameActiveX01Event.NavigateBack)
        }
    }

    private fun validateKey(key: Int): Boolean {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return false

        return state.currentInputScore.addDecimal(key) <= currentPlayerScore.scoreLeft
    }

    companion object {
        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
