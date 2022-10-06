package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.x01.ui.model.GameMode
import com.pointlessapps.dartify.compose.game.setup.x01.ui.model.MatchResolutionStrategy
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameSetupX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameSetupX01Event
}

internal data class GameSetupX01State(
    val matchResolutionStrategy: MatchResolutionStrategy = MatchResolutionStrategy.FirstTo(1, 3),
    val startingScore: Int = 501,
    val inMode: GameMode = GameMode.Straight,
    val outMode: GameMode = GameMode.Double,
    val players: List<Player> = listOf(
        Player("You"),
        Bot(75f, "CPU (avg. 75)"),
    ),
)

internal class GameSetupX01ViewModel : ViewModel() {

    var state by mutableStateOf(GameSetupX01State())
        private set

    private val eventChannel = Channel<GameSetupX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun onStartGameClicked() {
        viewModelScope.launch {
            eventChannel.send(
                GameSetupX01Event.Navigate(
                    Route.GameActive.X01(
                        GameSettings(
                            startingScore = state.startingScore,
                            numberOfSets = state.matchResolutionStrategy.numberOfSets,
                            numberOfLegs = state.matchResolutionStrategy.numberOfLegs,
                            resolutionPredicate = state.matchResolutionStrategy.resolutionPredicate(),
                            players = state.players,
                        ),
                    ),
                ),
            )
        }
    }

    fun setStartingScore(score: Int?) {
        if (score == null || !validateStartingScore(score)) {
            // TODO show error snackbar
            return
        }

        state = state.copy(
            startingScore = score,
        )
    }

    fun onInGameModeSelected(gameMode: GameMode) {
        state = state.copy(
            inMode = gameMode,
        )
    }

    fun onOutGameModeSelected(gameMode: GameMode) {
        state = state.copy(
            outMode = gameMode,
        )
    }

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            eventChannel.send(GameSetupX01Event.Navigate(Route.Players))
        }
    }

    fun onMatchResolutionStrategyChanged(matchResolutionStrategy: MatchResolutionStrategy?) {
        if (matchResolutionStrategy == null) {
            return
        }

        state = state.copy(
            matchResolutionStrategy = matchResolutionStrategy,
        )
    }

    fun onNumberOfSetsChanged(change: Int) {
        state = state.copy(
            matchResolutionStrategy = state.matchResolutionStrategy.let {
                it.copy(numberOfSets = it.numberOfSets + change)
            },
        )
    }

    fun onNumberOfLegsChanged(change: Int) {
        state = state.copy(
            matchResolutionStrategy = state.matchResolutionStrategy.let {
                it.copy(numberOfLegs = it.numberOfLegs + change)
            },
        )
    }

    // TODO accept only X01 values
    private fun validateStartingScore(score: Int) =
        score in MIN_STARTING_SCORE..MAX_STARTING_SCORE

    companion object {
        // TODO probably move to settings
        val STARTING_SCORES = listOf(301, 501, 701)

        const val MIN_STARTING_SCORE = 101
        const val MAX_STARTING_SCORE = 901
    }
}
