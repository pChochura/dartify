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
import com.pointlessapps.dartify.domain.game.x01.score.usecase.*
import com.pointlessapps.dartify.domain.game.x01.turn.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameActiveX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameActiveX01Event
    object NavigateBack : GameActiveX01Event

    data class AskForNumberOfThrowsAndDoubles(
        val availableThrowMin: Int,
        val availableDoubleMax: Map<Int, Int>,
    ) : GameActiveX01Event

    @JvmInline
    value class AskForNumberOfThrows(val availableThrowMin: Int) : GameActiveX01Event

    @JvmInline
    value class AskForNumberOfDoubles(val availableMax: Int) : GameActiveX01Event

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
    private val shouldAsForNumberOfDoublesUseCase: ShouldAsForNumberOfDoublesUseCase,
    private val calculateMinNumberOfThrowsUseCase: CalculateMinNumberOfThrowsUseCase,
    private val calculateMaxNumberOfDoublesUseCase: CalculateMaxNumberOfDoublesUseCase,
    private val calculateMaxNumberOfDoublesForThreeThrowsUseCase: CalculateMaxNumberOfDoublesForThreeThrowsUseCase,
    private val nextTurnUseCase: NextTurnUseCase,
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
            gameSettings.numberOfSets,
            gameSettings.numberOfLegs,
            gameSettings.matchResolutionStrategy.toMatchResolutionStrategy(),
        )

        state = state.copy(
            currentInputScore = currentState.score ?: state.currentInputScore,
            currentSet = currentState.set ?: state.currentSet,
            currentLeg = currentState.leg ?: state.currentLeg,
            currentPlayer = currentState.player?.fromPlayer() ?: state.currentPlayer,
            playersScores = currentState.playerScores?.map { it.fromPlayerScore() }
                ?: state.playersScores,
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
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return

        if (
            !validateScoreUseCase(
                quickScore,
                currentPlayerScore.scoreLeft - quickScore,
                gameSettings.startingScore,
                gameSettings.inMode.toInMode(),
                currentPlayerScore.player.outMode.toOutMode(),
            )
        ) {
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
            currentInputScore = currentState.score ?: state.currentInputScore,
            currentSet = currentState.set ?: state.currentSet,
            currentLeg = currentState.leg ?: state.currentLeg,
            currentPlayer = currentState.player?.fromPlayer() ?: state.currentPlayer,
            playersScores = currentState.playerScores?.map { it.fromPlayerScore() }
                ?: state.playersScores,
        )
    }

    fun onDoneClicked() {
        val currentPlayerScore = state.playersScores.find {
            it.player.id == state.currentPlayer?.id
        } ?: return

        if (
            !validateScoreUseCase(
                state.currentInputScore,
                currentPlayerScore.scoreLeft - state.currentInputScore,
                gameSettings.startingScore,
                gameSettings.inMode.toInMode(),
                currentPlayerScore.player.outMode.toOutMode(),
            )
        ) {
            // TODO show error snackbar
            return
        }

        val shouldAskForNumberOfDoubles = shouldAsForNumberOfDoublesUseCase(
            currentPlayerScore.scoreLeft,
            currentPlayerScore.scoreLeft - state.currentInputScore,
            outMode = currentPlayerScore.player.outMode.toOutMode(),
        )
        if (currentPlayerScore.scoreLeft == state.currentInputScore) {
            viewModelScope.launch {
                eventChannel.send(
                    if (shouldAskForNumberOfDoubles) {
                        GameActiveX01Event.AskForNumberOfThrowsAndDoubles(
                            availableThrowMin = calculateMinNumberOfThrowsUseCase(
                                currentPlayerScore.scoreLeft,
                                currentPlayerScore.player.outMode.toOutMode(),
                            ),
                            availableDoubleMax = calculateMaxNumberOfDoublesUseCase(
                                currentPlayerScore.scoreLeft,
                            ),
                        )
                    } else {
                        GameActiveX01Event.AskForNumberOfThrows(
                            availableThrowMin = calculateMinNumberOfThrowsUseCase(
                                currentPlayerScore.scoreLeft,
                                currentPlayerScore.player.outMode.toOutMode(),
                            ),
                        )
                    },
                )
            }

            return
        }

        if (shouldAskForNumberOfDoubles) {
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.AskForNumberOfDoubles(
                        calculateMaxNumberOfDoublesForThreeThrowsUseCase(currentPlayerScore.scoreLeft),
                    ),
                )
            }

            return
        }

        addInputUseCase(state.currentInputScore, 3, 0)
        val currentState = nextTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score ?: state.currentInputScore,
            currentSet = currentState.set ?: state.currentSet,
            currentLeg = currentState.leg ?: state.currentLeg,
            currentPlayer = currentState.player?.fromPlayer() ?: state.currentPlayer,
            playersScores = currentState.playerScores?.map { it.fromPlayerScore() }
                ?: state.playersScores,
        )
    }

    fun onClearClicked() {
        state = state.copy(
            currentInputScore = 0,
        )
    }

    fun onNumberOfDoublesClicked(numberOfDoubles: Int) {
        addInputUseCase(state.currentInputScore, 3, numberOfThrowsOnDouble = numberOfDoubles)
        val currentState = nextTurnUseCase()
        state = state.copy(
            currentInputScore = currentState.score ?: state.currentInputScore,
            currentSet = currentState.set ?: state.currentSet,
            currentLeg = currentState.leg ?: state.currentLeg,
            currentPlayer = currentState.player?.fromPlayer() ?: state.currentPlayer,
            playersScores = currentState.playerScores?.map { it.fromPlayerScore() }
                ?: state.playersScores,
        )
    }

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfThrowsOnDoubles: Int = 0) {
        val currentState = finishLegUseCase(numberOfThrows, numberOfThrowsOnDoubles)

        if (currentState.won == true) {
            val player = requireNotNull(currentState.player).fromPlayer()
            val playerScore = requireNotNull(
                currentState.playerScores?.find {
                    it.player.id == player.id
                }?.fromPlayerScore(),
            )

            viewModelScope.launch {
                eventChannel.send(GameActiveX01Event.ShowWinnerDialog(playerScore))
            }

            return
        }

        state = state.copy(
            currentInputScore = currentState.score ?: state.currentInputScore,
            currentSet = currentState.set ?: state.currentSet,
            currentLeg = currentState.leg ?: state.currentLeg,
            currentPlayer = currentState.player?.fromPlayer() ?: state.currentPlayer,
            playersScores = currentState.playerScores?.map { it.fromPlayerScore() }
                ?: state.playersScores,
        )
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
