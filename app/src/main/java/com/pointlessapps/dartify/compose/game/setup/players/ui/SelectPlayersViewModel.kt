package com.pointlessapps.dartify.compose.game.setup.players.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.Player
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val ADD_CPU_ID = 0L

internal sealed interface SelectPlayersEvent {
    @JvmInline
    value class OnPlayersSelected(val players: List<Player>) : SelectPlayersEvent
    object AskForCpuAverage : SelectPlayersEvent
    object AskForPlayerName : SelectPlayersEvent
}

internal data class SelectPlayersState(
    val selectedPlayers: List<Player> = emptyList(),
    val allPlayers: List<Player> = listOf(
        Bot(id = ADD_CPU_ID, average = 0f, name = "CPU"),
    ),
)

internal class SelectPlayersViewModel : ViewModel() {

    var state by mutableStateOf(SelectPlayersState())
        private set

    private val eventChannel = Channel<SelectPlayersEvent>()
    val events = eventChannel.receiveAsFlow()

    fun setSelectedPlayers(selectedPlayers: List<Player>) {
        val selectedPlayerIds = selectedPlayers.map(Player::id).toSet()
        state = state.copy(
            selectedPlayers = selectedPlayers,
            allPlayers = state.allPlayers.filter { it.id !in selectedPlayerIds },
        )
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            eventChannel.send(SelectPlayersEvent.OnPlayersSelected(state.selectedPlayers))
        }
    }

    fun onPlayerClicked(player: Player) {
        if (player is Bot && player.id == ADD_CPU_ID) {
            viewModelScope.launch {
                eventChannel.send(SelectPlayersEvent.AskForCpuAverage)
            }

            return
        }

        val selectedPlayer = state.selectedPlayers.find { it.id == player.id }
        if (selectedPlayer is Bot) {
            state = state.copy(
                selectedPlayers = state.selectedPlayers - player,
            )

            return
        }

        state = if (selectedPlayer != null) {
            state.copy(
                selectedPlayers = state.selectedPlayers - player,
                allPlayers = state.allPlayers + player,
            )
        } else {
            state.copy(
                selectedPlayers = state.selectedPlayers + player,
                allPlayers = state.allPlayers - player,
            )
        }
    }

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            eventChannel.send(SelectPlayersEvent.AskForPlayerName)
        }
    }

    fun onPlayerAdded(player: Player) {
        state = state.copy(
            selectedPlayers = state.selectedPlayers + player,
        )
    }
}
