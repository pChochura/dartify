package com.pointlessapps.dartify.compose.game.setup.x01.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.*
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel.Companion.DEFAULT_NUMBER_OF_LEGS
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel.Companion.DEFAULT_NUMBER_OF_SETS
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01ViewModel.Companion.DEFAULT_STARTING_SCORE
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.mutableStateOf
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import com.pointlessapps.dartify.reorderable.list.ItemInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Integer.min

internal sealed interface GameSetupX01Event {
    @JvmInline
    value class Navigate(val route: Route) : GameSetupX01Event

    @JvmInline
    value class ShowErrorSnackbar(@StringRes val message: Int) : GameSetupX01Event
}

@Immutable
internal data class GameSetupX01State(
    val matchResolutionStrategy: MatchResolutionStrategy = MatchResolutionStrategy.FirstTo,
    val numberOfSets: Int = DEFAULT_NUMBER_OF_SETS,
    val numberOfLegs: Int = DEFAULT_NUMBER_OF_LEGS,
    val startingScore: Int = DEFAULT_STARTING_SCORE,
    val inMode: GameMode = GameMode.Straight,
    val outMode: GameMode = GameMode.Double,
    val players: List<Player> = emptyList(),
)

internal class GameSetupX01ViewModel(
    savedStateHandle: SavedStateHandle,
    private val vibrateUseCase: VibrateUseCase,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(GameSetupX01State())
        private set

    private val eventChannel = Channel<GameSetupX01Event>()
    val events = eventChannel.receiveAsFlow()

    fun setSelectedPlayers(players: List<Player>) {
        state = state.copy(players = players)
    }

    fun onStartGameClicked() {
        if (state.players.size != 2) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameSetupX01Event.ShowErrorSnackbar(R.string.only_two_players_game_is_supported),
                )
            }

            return
        }

        viewModelScope.launch {
            eventChannel.send(
                GameSetupX01Event.Navigate(
                    Route.GameActive.X01(
                        GameSettings(
                            startingScore = state.startingScore,
                            numberOfSets = state.numberOfSets,
                            numberOfLegs = state.numberOfLegs,
                            matchResolutionStrategy = state.matchResolutionStrategy,
                            players = state.players.map {
                                if (it.outMode == null) {
                                    it.copy(outMode = state.outMode)
                                } else {
                                    it
                                }
                            },
                            inMode = state.inMode,
                        ),
                    ),
                ),
            )
        }
    }

    fun setStartingScore(score: Int?) {
        if (score == null || !validateStartingScore(score)) {
            vibrateUseCase.error()
            viewModelScope.launch {
                eventChannel.send(
                    GameSetupX01Event.ShowErrorSnackbar(R.string.you_can_input_x01_values),
                )
            }

            return
        }

        state = state.copy(
            startingScore = score,
        )
    }

    fun onInGameModeSelected(gameMode: GameMode?) {
        state = state.copy(
            inMode = requireNotNull(gameMode),
        )
    }

    fun onOutGameModeSelected(gameMode: GameMode?) {
        state = state.copy(
            outMode = requireNotNull(gameMode),
        )
    }

    fun onOutGameModeSelectedForPlayer(gameMode: GameMode?, player: Player) {
        state = state.copy(
            players = state.players.map {
                if (it.id == player.id) {
                    it.copy(outMode = gameMode)
                } else {
                    it
                }
            },
        )
    }

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            eventChannel.send(GameSetupX01Event.Navigate(Route.Players(state.players)))
        }
    }

    fun onMatchResolutionStrategyChanged(matchResolutionStrategy: MatchResolutionStrategy?) {
        if (matchResolutionStrategy == null) {
            return
        }

        state = state.copy(
            matchResolutionStrategy = matchResolutionStrategy,
            numberOfSets = min(
                matchResolutionStrategy.convertValueFrom(
                    state.matchResolutionStrategy,
                    state.numberOfSets,
                ),
                MAX_NUMBER_OF_SETS,
            ),
            numberOfLegs = min(
                matchResolutionStrategy.convertValueFrom(
                    state.matchResolutionStrategy,
                    state.numberOfLegs,
                ),
                MAX_NUMBER_OF_LEGS,
            ),
        )
    }

    fun onNumberOfSetsChanged(change: Int) {
        val realChange = when (state.matchResolutionStrategy) {
            MatchResolutionStrategy.FirstTo -> change
            MatchResolutionStrategy.BestOf -> change * 2
        }

        state = state.copy(
            numberOfSets = state.numberOfSets + realChange,
        )
    }

    fun onNumberOfLegsChanged(change: Int) {
        val realChange = when (state.matchResolutionStrategy) {
            MatchResolutionStrategy.FirstTo -> change
            MatchResolutionStrategy.BestOf -> change * 2
        }

        state = state.copy(
            numberOfLegs = state.numberOfLegs + realChange,
        )
    }

    fun onPlayersSwapped(from: ItemInfo, to: ItemInfo) {
        vibrateUseCase.tick()

        val fromIndex = state.players.indexOfFirst { it.id == from.key }
            .takeIf { it != -1 } ?: return
        val toIndex = state.players.indexOfFirst { it.id == to.key }
            .takeIf { it != -1 } ?: return
        state = state.copy(
            players = state.players.toMutableList().apply {
                add(toIndex, removeAt(fromIndex))
            },
        )
    }

    fun onDragStarted() {
        vibrateUseCase.click()
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

        const val MIN_NUMBER_OF_SETS = 1
        const val MAX_NUMBER_OF_SETS = 5
        const val MIN_NUMBER_OF_LEGS = 1
        const val MAX_NUMBER_OF_LEGS = 9
        const val DEFAULT_NUMBER_OF_SETS = 1
        const val DEFAULT_NUMBER_OF_LEGS = 3
    }
}
