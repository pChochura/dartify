package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel.Companion.EMPTY_PLAYER
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameActiveX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameActiveX01Event
    object NavigateBack : GameActiveX01Event

    data class AskForNumberOfThrows(
        val availableThrowMin: Int,
        val availableDoubleMax: Int,
    ) : GameActiveX01Event

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

internal class GameActiveX01ViewModel : ViewModel() {

    private lateinit var gameSettings: GameSettings
    private var startingPlayerIndex = 0
    private var quickScoreProvided = false

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

    fun onQuickScoreClicked(quickScore: Int) {
        if (!isScoreInRange(quickScore) || !validateScore(quickScore)) {
            return
        }

        quickScoreProvided = true
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
            currentInputScore = if (quickScoreProvided) {
                key
            } else {
                state.currentInputScore * 10 + key
            },
        )
        quickScoreProvided = false
    }

    fun onUndoClicked() {
        if (state.currentInputScore != 0) {
            quickScoreProvided = false
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
            quickScoreProvided = false
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

        quickScoreProvided = false
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

        if (currentPlayerScore.scoreLeft == state.currentInputScore) {
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.AskForNumberOfThrows(
                        availableThrowMin = calculateAvailableMinNumberOfThrows(),
                        availableDoubleMax = calculateAvailableMaxNumberOfDoubles(),
                    ),
                )
            }

            return
        }

        if (!validateScore(state.currentInputScore)) {
            // TODO show error snackbar
            return
        }

        if (currentPlayerScore.scoreLeft - state.currentInputScore <= SCORE_TO_ASK_FOR_DOUBLES) {
            viewModelScope.launch {
                eventChannel.send(
                    GameActiveX01Event.AskForNumberOfDoubles(calculateAvailableMaxNumberOfDoubles()),
                )
            }

            return
        }

        currentPlayerScore.addInput(state.currentInputScore)
        setNextTurn()
    }

    fun onClearClicked() {
        quickScoreProvided = false
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

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfDoubles: Int) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return

        if (!validateScore(state.currentInputScore, numberOfThrows)) {
            // TODO show error snackbar
            return
        }

        performLegFinished(numberOfThrows, numberOfDoubles, currentPlayerScore)
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

        quickScoreProvided = false
        state = state.copy(
            currentPlayer = nextPlayer ?: state.playersScores[nextPlayerScoreIndex].player,
            currentSet = state.currentSet + if (isSetFinished) setIncrement else 0,
            currentLeg = if (isSetFinished) 1 else state.currentLeg + legIncrement,
            currentInputScore = 0,
        )
    }

    private fun validateScore(score: Int, numberOfThrows: Int = 3): Boolean {
        val oneThrowRange = (0..20).toSet()
        val oneThrowScores = oneThrowRange +
                oneThrowRange.map { it * 2 } +
                oneThrowRange.map { it * 3 } +
                setOf(25, 50)

        val twoTrowsScores = oneThrowScores.flatMap { firstThrowScore ->
            oneThrowScores.map { secondThrowScore -> firstThrowScore + secondThrowScore }
        }.toSet()

        val threeTrowsScores = twoTrowsScores.flatMap { firstAndSecondThrowsScore ->
            oneThrowScores.map { thirdThrowScore -> firstAndSecondThrowsScore + thirdThrowScore }
        }.toSet()

        return when (numberOfThrows) {
            1 -> score in oneThrowScores
            2 -> score in twoTrowsScores
            3 -> score in threeTrowsScores
            else -> false
        }
    }

    private fun validateKey(key: Int) =
        quickScoreProvided || isScoreInRange(state.currentInputScore * 10 + key)

    private fun isScoreInRange(score: Int): Boolean {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return false

        return score <= minOf(currentPlayerScore.scoreLeft, MAX_SCORE) &&
                currentPlayerScore.scoreLeft - score != 1
    }

    private fun calculateAvailableMinNumberOfThrows() = 1

    private fun calculateAvailableMaxNumberOfDoubles() = 3

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
        const val MAX_SCORE = 180
        const val SCORE_TO_ASK_FOR_DOUBLES = 50

        val EMPTY_PLAYER = Player("")

        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
