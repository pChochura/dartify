package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.game.setup.x01.model.GameMode
import com.pointlessapps.dartify.compose.game.setup.x01.model.MatchResolutionStrategy
import com.pointlessapps.dartify.compose.game.setup.x01.model.MatchResolutionStrategy.Companion.DEFAULT_NUMBER_OF_LEGS
import com.pointlessapps.dartify.compose.game.setup.x01.model.MatchResolutionStrategy.Companion.DEFAULT_NUMBER_OF_SETS
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel.Companion.DEFAULT_STARTING_SCORE
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface GameSetupX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameSetupX01Event
}

internal data class GameSetupX01State(
    val matchResolutionStrategy: MatchResolutionStrategy = MatchResolutionStrategy.FirstTo(
        numberOfSets = DEFAULT_NUMBER_OF_SETS,
        numberOfLegs = DEFAULT_NUMBER_OF_LEGS,
    ),
    val startingScore: Int = DEFAULT_STARTING_SCORE,
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

    fun onMatchResolutionStrategyChanged(matchResolutionStrategyType: MatchResolutionStrategy.Type?) {
        if (matchResolutionStrategyType == null) {
            return
        }

        state = state.copy(
            matchResolutionStrategy = when (matchResolutionStrategyType) {
                MatchResolutionStrategy.Type.FirstTo -> MatchResolutionStrategy.FirstTo(
                    numberOfSets = DEFAULT_NUMBER_OF_SETS,
                    numberOfLegs = DEFAULT_NUMBER_OF_LEGS,
                )
                MatchResolutionStrategy.Type.BestOf -> MatchResolutionStrategy.BestOf(
                    numberOfSets = DEFAULT_NUMBER_OF_SETS,
                    numberOfLegs = DEFAULT_NUMBER_OF_LEGS,
                )
            },
        )
    }

    fun onNumberOfSetsChanged(change: Int) {
        state = state.copy(
            matchResolutionStrategy = state.matchResolutionStrategy.withNumberOfSetsChanged(change),
        )
    }

    fun onNumberOfLegsChanged(change: Int) {
        state = state.copy(
            matchResolutionStrategy = state.matchResolutionStrategy.withNumberOfLegsChanged(change),
        )
    }

    @Suppress("MagicNumber")
    private fun validateStartingScore(score: Int) =
        score in MIN_STARTING_SCORE..MAX_STARTING_SCORE &&
                (score - 1) % 100 == 0

    companion object {
        // TODO probably move to settings
        val STARTING_SCORES = listOf(301, 501, 701)

        const val MIN_STARTING_SCORE = 101
        const val MAX_STARTING_SCORE = 901

        const val DEFAULT_STARTING_SCORE = 501
    }
}
