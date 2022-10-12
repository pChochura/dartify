package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel.Companion.EMPTY_PLAYER
import com.pointlessapps.dartify.compose.game.mappers.toInMode
import com.pointlessapps.dartify.compose.game.mappers.toOutMode
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.domain.game.x01.usecase.*
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
    val currentPlayer: Player = EMPTY_PLAYER,
    val currentInputScore: Int = 0,
)

internal class GameActiveX01ViewModel(
    private val validateScoreUseCase: ValidateScoreUseCase,
    private val isCheckoutPossibleUseCase: IsCheckoutPossibleUseCase,
    private val shouldAsForNumberOfDoublesUseCase: ShouldAsForNumberOfDoublesUseCase,
    private val calculateMinNumberOfThrowsUseCase: CalculateMinNumberOfThrowsUseCase,
    private val calculateMaxNumberOfDoublesUseCase: CalculateMaxNumberOfDoublesUseCase,
    private val calculateMaxNumberOfDoublesForThreeThrowsUseCase: CalculateMaxNumberOfDoublesForThreeThrowsUseCase,
) : ViewModel() {

    private lateinit var gameSettings: GameSettings
    private var startingPlayerIndex = 0

    var state by mutableStateOf(GameActiveX01State())
        private set

    private val eventChannel = Channel<GameActiveX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun setGameSettings(gameSettings: GameSettings) {
        this.gameSettings = gameSettings
        val players = gameSettings.players.map {
            PlayerScore(player = it, startingScore = gameSettings.startingScore)
        }

        state = state.copy(
            playersScores = players,
            currentPlayer = players[startingPlayerIndex].player,
        )
    }

    fun onPossibleCheckoutRequested(): Int? {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
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

        return playerScore.scoreLeft - if (player == state.currentPlayer) {
            state.currentInputScore
        } else {
            0
        }
    }

    fun onQuickScoreClicked(quickScore: Int) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
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
            currentInputScore = state.currentInputScore * 10 + key,
        )
    }

    fun onUndoClicked() {
        if (state.currentInputScore != 0) {
            state = state.copy(
                currentInputScore = 0,
            )

            return
        }

        val currentPlayerScoreIndex = state.playersScores.indexOfFirst {
            it.player == state.currentPlayer
        }
        val currentPlayerScore = state.playersScores[currentPlayerScoreIndex]

        val prevPlayerScoreIndex = currentPlayerScoreIndex.prevPlayerIndex()
        var prevPlayerScore = state.playersScores[prevPlayerScoreIndex]

        if (prevPlayerScore.hasNoInputs()) {
            state = state.copy(
                currentInputScore = 0,
            )

            return
        }

        // Set or leg was reverted
        if (prevPlayerScore.numberOfDarts == 0) {
            if (currentPlayerScore.hasWonPreviousLeg()) {
                prevPlayerScore = state.playersScores[currentPlayerScoreIndex]
            }
            startingPlayerIndex = startingPlayerIndex.prevPlayerIndex()

            state.playersScores.forEach {
                if (it != prevPlayerScore) {
                    it.markLegAsReverted()
                }
            }
        }

        val prevPlayerInputScore = prevPlayerScore.popInput()

        val currentSet = state.playersScores.sumOf { it.wonSets } + 1
        val currentLeg = state.playersScores.sumOf { it.wonLegs } + 1

        state = state.copy(
            currentInputScore = prevPlayerInputScore,
            currentSet = currentSet,
            currentLeg = currentLeg,
            currentPlayer = prevPlayerScore.player,
        )
    }

    fun onDoneClicked() {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
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

        currentPlayerScore.addInput(state.currentInputScore)
        setNextTurn()
    }

    fun onClearClicked() {
        state = state.copy(
            currentInputScore = 0,
        )
    }

    fun onNumberOfDoublesClicked(numberOfDoubles: Int) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return

        currentPlayerScore.addInput(state.currentInputScore, doubleThrowTries = numberOfDoubles)
        setNextTurn()
    }

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfDoubles: Int = 0) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return

        performLegFinished(numberOfThrows, numberOfDoubles, currentPlayerScore)
    }

    fun getCurrentFinishSuggestion(): String? {
        val playerScore = state.playersScores.find {
            it.player == state.currentPlayer
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

    private fun setNextTurn(
        isLegFinished: Boolean = false,
        isSetFinished: Boolean = false,
        nextPlayer: Player? = null,
    ) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return

        val currentPlayerScoreIndex = state.playersScores.indexOf(currentPlayerScore)
        val nextPlayerScoreIndex = currentPlayerScoreIndex.nextPlayerIndex()

        val setIncrement = if (isSetFinished) 1 else 0
        val legIncrement = if (isLegFinished) 1 else 0

        state = state.copy(
            currentPlayer = nextPlayer ?: state.playersScores[nextPlayerScoreIndex].player,
            currentSet = state.currentSet + if (isSetFinished) setIncrement else 0,
            currentLeg = if (isSetFinished) 1 else state.currentLeg + legIncrement,
            currentInputScore = 0,
        )
    }

    private fun validateKey(key: Int): Boolean {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return false

        return state.currentInputScore * 10 + key <= currentPlayerScore.scoreLeft
    }

    private fun performLegFinished(
        numberOfThrows: Int,
        numberOfDoubles: Int,
        playerScore: PlayerScore,
    ): Boolean {
        val isSetFinished = gameSettings.matchResolutionStrategy
            .resolutionPredicate(gameSettings.numberOfLegs)
            .invoke(playerScore.wonLegs + 1)

        val isMatchFinished = isSetFinished && gameSettings.matchResolutionStrategy
            .resolutionPredicate(gameSettings.numberOfSets)
            .invoke(playerScore.wonSets + 1)

        playerScore.addInput(
            state.currentInputScore,
            weight = numberOfThrows,
            doubleThrowTries = numberOfDoubles,
        )

        state.playersScores.forEach {
            if (isSetFinished) {
                it.markSetAsFinished(it == playerScore)
            } else {
                it.markLegAsFinished(it == playerScore)
            }
        }

        if (isMatchFinished) {
            viewModelScope.launch {
                eventChannel.send(GameActiveX01Event.ShowWinnerDialog(playerScore))
            }

            return true
        }

        startingPlayerIndex = startingPlayerIndex.nextPlayerIndex()

        setNextTurn(
            isSetFinished = isSetFinished,
            isLegFinished = true,
            nextPlayer = state.playersScores[startingPlayerIndex].player,
        )

        return false
    }

    private fun Int.nextPlayerIndex() = (this + 1) % gameSettings.players.size
    private fun Int.prevPlayerIndex() =
        (this + gameSettings.players.size - 1) % gameSettings.players.size

    companion object {
        val EMPTY_PLAYER = Player("")

        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
