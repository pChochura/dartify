package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel.Companion.EMPTY_PLAYER
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

internal sealed interface GameActiveX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameActiveX01Event
}

internal data class GameActiveX01State(
    val currentSet: Int = 1,
    val currentLeg: Int = 1,
    val startingScore: Int = 501,
    val playersScores: List<PlayerScore> = emptyList(),
    val currentPlayer: Player = EMPTY_PLAYER,
    val currentInputScore: Int = 0,
)

internal class GameActiveX01ViewModel : ViewModel() {

    private var quickScoreProvided = false

    var state by mutableStateOf(GameActiveX01State())
        private set

    private val eventChannel = Channel<GameActiveX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun setGameSettings(gameSettings: GameSettings) {
        val players = gameSettings.players.map(::PlayerScore)

        state = state.copy(
            startingScore = gameSettings.startingScore,
            playersScores = players,
            currentPlayer = players.first().player,
        )
    }

    fun onQuickScoreClicked(quickScore: Int) {
        quickScoreProvided = true
        state = state.copy(
            currentInputScore = quickScore,
        )
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

        val prevPlayerInputScore = prevPlayerScore.popInputs(3)

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

        currentPlayerScore.addInputs(0, 0, state.currentInputScore)
        val currentPlayerScoreIndex = state.playersScores.indexOf(currentPlayerScore)
        val nextPlayerScoreIndex = (currentPlayerScoreIndex + 1) % state.playersScores.size
        val nextPlayer = state.playersScores[nextPlayerScoreIndex].player

        quickScoreProvided = false
        state = state.copy(
            currentInputScore = 0,
            currentPlayer = nextPlayer,
        )
    }

    fun onClearClicked() {
        quickScoreProvided = false
        state = state.copy(
            currentInputScore = 0,
        )
    }

    private fun validateKey(key: Int) =
        quickScoreProvided || state.currentInputScore * 10 + key <= 180

    companion object {
        val EMPTY_PLAYER = Player("")

        val QUICK_SCORES = listOf(
            26, 41, 45, 60, 85, 100,
        )
    }
}
