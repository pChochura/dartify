package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.active.x01.model.InputMode
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore.Dart.Companion.MAX_NUMBER_OF_THROWS
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.model.score
import com.pointlessapps.dartify.compose.game.mappers.*
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.addDecimal
import com.pointlessapps.dartify.domain.game.x01.score.usecase.IsCheckoutPossibleUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateScoreUseCase
import com.pointlessapps.dartify.domain.game.x01.score.usecase.ValidateSingleThrowUseCase
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
    val currentInputScore: InputScore? = null,
    val inputMode: InputMode = InputMode.PerDart,
)

internal class GameActiveX01ViewModel(
    private val validateSingleThrowUseCase: ValidateSingleThrowUseCase,
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
            currentInputScore = currentState.score?.fromInputScore(),
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
            state.currentInputScore.score()
        } else {
            0
        }
    }

    fun onQuickScoreClicked(quickScore: Int) {
        if (!validateScoreUseCase(quickScore) && state.inputMode == InputMode.PerTurn) {
            return
        }

        state = state.copy(
            currentInputScore = InputScore.Turn(quickScore),
        )

        onDoneClicked()
    }

    fun onKeyClicked(key: Int, multiplier: Int = 1) {
        if (!validateKey(key, multiplier)) {
            return
        }

        if (
            state.inputMode == InputMode.PerDart &&
            (state.currentInputScore as? InputScore.Dart)?.scores?.size == MAX_NUMBER_OF_THROWS
        ) {
            // TODO show an error snackbar
            return
        }

        state = state.copy(
            currentInputScore = when (state.inputMode) {
                InputMode.PerTurn -> InputScore.Turn(
                    state.currentInputScore.score().addDecimal(key),
                )
                InputMode.PerDart -> InputScore.Dart(
                    when (val score = state.currentInputScore) {
                        is InputScore.Dart -> score.scores
                        is InputScore.Turn -> listOf(score.score)
                        null -> emptyList()
                    } + key,
                )
            },
        )
    }

    fun onUndoClicked() {
        if (invokeSingleUndoAction()) {
            return
        }

        val currentState = undoTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
            currentSet = currentState.set,
            currentLeg = currentState.leg,
            currentPlayer = currentState.player.fromPlayer(),
            playersScores = currentState.playerScores.map { it.fromPlayerScore() },
        )
    }

    private fun invokeSingleUndoAction(): Boolean {
        when (state.inputMode) {
            InputMode.PerDart -> {
                val score = state.currentInputScore
                if (score is InputScore.Dart && score.scores.isNotEmpty()) {
                    state = state.copy(
                        currentInputScore = InputScore.Dart(score.scores.dropLast(1)),
                    )

                    return true
                } else if (score is InputScore.Turn && score.score != 0) {
                    state = state.copy(
                        currentInputScore = null,
                    )

                    return true
                }
            }
            InputMode.PerTurn -> {
                if (state.currentInputScore.score() != 0) {
                    state = state.copy(
                        currentInputScore = null,
                    )

                    return true
                }
            }
        }

        return false
    }

    fun onDoneClicked() {
        if (!validateScoreUseCase(state.currentInputScore?.toInputScore())) {
            // TODO show error snackbar
            return
        }

        viewModelScope.launch {
            when (val event = doneTurnUseCase(state.currentInputScore.score())) {
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
                    addInputUseCase(
                        requireNotNull(state.currentInputScore).toInputScore(),
                        numberOfThrowsOnDouble = 0,
                    )
                    val currentState = nextTurnUseCase()
                    state = state.copy(
                        currentInputScore = currentState.score?.fromInputScore(),
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
            currentInputScore = null,
        )
    }

    fun onNumberOfDoublesClicked(numberOfDoubles: Int) {
        addInputUseCase(
            requireNotNull(state.currentInputScore).toInputScore(),
            numberOfThrowsOnDouble = numberOfDoubles,
        )
        val currentState = nextTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score?.fromInputScore(),
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
                    currentInputScore = state.score?.fromInputScore(),
                    currentSet = state.set,
                    currentLeg = state.leg,
                    currentPlayer = state.player.fromPlayer(),
                    playersScores = state.playerScores.map { it.fromPlayerScore() },
                )
            }
        }
    }

    fun onChangeInputModeClicked() {
        state = state.copy(
            inputMode = when (state.inputMode) {
                InputMode.PerDart -> InputMode.PerTurn
                InputMode.PerTurn -> InputMode.PerDart
            },
        )
    }

    fun getCurrentFinishSuggestion(): String? {
        val playerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return null

        // TODO add actual implementation of this
        if (playerScore.scoreLeft == 101) {
            return "T20 D20 D1"
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

    private fun validateKey(key: Int, multiplier: Int): Boolean {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return false

        return when (state.inputMode) {
            InputMode.PerDart -> {
                validateSingleThrowUseCase(
                    key,
                    multiplier,
                    currentPlayerScore.scoreLeft - state.currentInputScore.score(),
                ) && state.currentInputScore.score() + key <= currentPlayerScore.scoreLeft
            }
            InputMode.PerTurn -> state.currentInputScore.score()
                .addDecimal(key) <= currentPlayerScore.scoreLeft
        }
    }

    companion object {
        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
