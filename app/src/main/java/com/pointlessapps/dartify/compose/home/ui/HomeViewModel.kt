package com.pointlessapps.dartify.compose.home.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal sealed interface HomeEvent {
    @JvmInline
    value class Navigate(val route: Route) : HomeEvent
}

internal class HomeViewModel : ViewModel() {

    private val eventChannel = Channel<HomeEvent>()
    val events = eventChannel.receiveAsFlow()

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

    fun onStatsClicked() = Unit

    fun onDailyChallengeClicked() = Unit

    fun onSettingsClicked() = Unit
}
