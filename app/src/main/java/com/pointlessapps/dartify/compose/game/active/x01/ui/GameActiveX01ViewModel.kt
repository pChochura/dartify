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

    @JvmInline
    value class AskForNumberOfThrows(val availableMin: Int) : GameActiveX01Event

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
        if (!isScoreInRange(quickScore)) {
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
        val currentPlayerScoreIndex = state.playersScores.indexOfFirst {
            it.player == state.currentPlayer
        }

        val prevPlayerScoreIndex =
            (currentPlayerScoreIndex + state.playersScores.size - 1) % state.playersScores.size
        val prevPlayerScore = state.playersScores[prevPlayerScoreIndex]

        if (prevPlayerScore.numberOfDarts == 0) {
            return
        }

        val prevPlayerInputScore = prevPlayerScore.popInput()

        quickScoreProvided = false
        state = state.copy(
            currentInputScore = prevPlayerInputScore,
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
                    GameActiveX01Event.AskForNumberOfThrows(calculateAvailableMinNumberOfThrows()),
                )
            }

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

        performFinishTurn(currentPlayerScore)
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

        currentPlayerScore.addDoubleThrowTries(numberOfDoubles)
        performFinishTurn(currentPlayerScore)
    }

    fun onNumberOfThrowsClicked(numberOfThrows: Int, numberOfDoubles: Int) {
        val currentPlayerScore = state.playersScores.find {
            it.player == state.currentPlayer
        } ?: return

        if (!performWin(numberOfThrows, numberOfDoubles, currentPlayerScore)) {
            performFinishTurn(currentPlayerScore)
        }
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

    private fun performFinishTurn(playerScore: PlayerScore) {
        playerScore.addInput(state.currentInputScore)
        val currentPlayerScoreIndex = state.playersScores.indexOf(playerScore)
        val nextPlayerScoreIndex = (currentPlayerScoreIndex + 1) % state.playersScores.size
        val nextPlayer = state.playersScores[nextPlayerScoreIndex].player

        quickScoreProvided = false
        state = state.copy(
            currentInputScore = 0,
            currentPlayer = nextPlayer,
        )
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

    private fun performWin(numberOfThrows: Int, numberOfDoubles: Int, playerScore: PlayerScore): Boolean {
        // TODO save stats
        val isNextSet = gameSettings.resolutionPredicate(
            gameSettings.numberOfSets,
            state.currentLeg,
        )
        val isFinished = isNextSet && gameSettings.resolutionPredicate(
            playerScore.wonSets + 1,
            playerScore.wonLegs + 1,
        )

        playerScore.addInput(state.currentInputScore, weight = numberOfThrows)
        playerScore.addDoubleThrowTries(numberOfDoubles)

        state.playersScores.forEach {
            if (isNextSet) {
                it.markSetAsFinished(it == playerScore)
            } else {
                it.markLegAsFinished(it == playerScore)
            }
        }

        if (isFinished) {
            viewModelScope.launch {
                eventChannel.send(GameActiveX01Event.ShowWinnerDialog(playerScore))
            }

            return true
        }

        startingPlayerIndex = (startingPlayerIndex + 1) % gameSettings.players.size

        state = state.copy(
            currentInputScore = 0,
            currentSet = if (isNextSet) state.currentSet + 1 else state.currentSet,
            currentLeg = if (isNextSet) 1 else state.currentLeg + 1,
            currentPlayer = state.playersScores[startingPlayerIndex].player,
        )

        return false
    }

    companion object {
        const val MAX_SCORE = 180
        const val SCORE_TO_ASK_FOR_DOUBLES = 50

        val EMPTY_PLAYER = Player("")

        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
