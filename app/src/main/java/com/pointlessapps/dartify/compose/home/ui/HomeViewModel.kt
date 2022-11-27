package com.pointlessapps.dartify.compose.home.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.home.mappers.fromActiveGame
import com.pointlessapps.dartify.compose.home.model.ActiveGame
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.mutableStateOf
import com.pointlessapps.dartify.domain.database.game.usecase.GetActiveGamesUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

internal sealed interface HomeEvent {
    data class Navigate(val route: Route) : HomeEvent
    data class ShowSnackbar(@StringRes val message: Int) : HomeEvent
}

@Immutable
@Parcelize
internal data class HomeState(
    val isLoading: Boolean = true,
    val favouriteGames: List<ActiveGame> = emptyList(),
) : Parcelable

internal class HomeViewModel(
    savedStateHandle: SavedStateHandle,
    private val getActiveGamesUseCase: GetActiveGamesUseCase,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(HomeState())
        private set

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

    fun refreshFavouriteGames() {
        getActiveGamesUseCase()
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach { activeGames ->
                state = state.copy(
                    isLoading = false,
                    favouriteGames = activeGames.map { it.fromActiveGame() },
                )
            }
            .catch {
                it.printStackTrace()
                eventChannel.send(HomeEvent.ShowSnackbar(R.string.something_went_wrong))
                state = state.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun onTrainingClicked() = Unit

    fun onPlayClicked() {
        viewModelScope.launch {
            eventChannel.send(
                HomeEvent.Navigate(
                    Route.GameSetup.X01(players = mutableStateOf(emptyList())),
                ),
            )
        }
    }

    fun onPlayClicked(activeGame: ActiveGame) {
        viewModelScope.launch {
            eventChannel.send(
                HomeEvent.Navigate(
                    Route.GameActive.X01(
                        GameSettings.LoadGame(
                            id = activeGame.gameId,
                            type = activeGame.type,
                        ),
                    ),
                ),
            )
        }
    }

    fun onStatsClicked() = Unit

    fun onDailyChallengeClicked() = Unit

    fun onSettingsClicked() = Unit
}
