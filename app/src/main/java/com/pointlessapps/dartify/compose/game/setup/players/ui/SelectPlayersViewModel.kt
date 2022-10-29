package com.pointlessapps.dartify.compose.game.setup.players.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Bot
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val ADD_CPU_ID = 0L

internal sealed interface SelectPlayersEvent {
    @JvmInline
    value class OnPlayersSelected(val players: List<Player>) : SelectPlayersEvent
    data class AskForCpuAverage(val bot: Bot?) : SelectPlayersEvent
    data class AskForPlayerName(val player: Player?) : SelectPlayersEvent

    data class ShowActionSnackbar(
        @StringRes val message: Int,
        @StringRes val actionLabel: Int,
        val actionCallback: () -> Unit,
    ) : SelectPlayersEvent
}

internal data class SelectPlayersState(
    val selectedPlayers: List<Player> = emptyList(),
    val allPlayers: List<Player> = listOf(
        Bot(id = ADD_CPU_ID, average = 0f, name = "CPU"),
    ),
)

internal class SelectPlayersViewModel(
    private val vibrateUseCase: VibrateUseCase,
) : ViewModel() {

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
                eventChannel.send(SelectPlayersEvent.AskForCpuAverage(null))
            }

            return
        }

        val selectedPlayer = state.selectedPlayers.find { it.id == player.id }
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

    fun onPlayerLongClicked(player: Player) {
        if (player.id == ADD_CPU_ID) {
            vibrateUseCase.error()

            return
        }

        vibrateUseCase.click()
        viewModelScope.launch {
            eventChannel.send(
                if (player is Bot) {
                    SelectPlayersEvent.AskForCpuAverage(player)
                } else {
                    SelectPlayersEvent.AskForPlayerName(player)
                },
            )
        }
    }

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            eventChannel.send(SelectPlayersEvent.AskForPlayerName(null))
        }
    }

    fun onPlayerAdded(player: Player) {
        state = state.copy(
            allPlayers = state.allPlayers.filter { it.id != player.id },
            selectedPlayers = state.selectedPlayers.filter { it.id != player.id } + player,
        )
    }

    fun onPlayerRemoved(player: Player) {
        val isSelected = state.selectedPlayers.find { it.id == player.id } != null
        state = state.copy(
            allPlayers = state.allPlayers - player,
            selectedPlayers = state.selectedPlayers - player,
        )

        vibrateUseCase.error()
        viewModelScope.launch {
            eventChannel.send(
                SelectPlayersEvent.ShowActionSnackbar(
                    R.string.player_has_been_removed,
                    R.string.undo,
                ) {
                    vibrateUseCase.click()
                    state = if (isSelected) {
                        state.copy(selectedPlayers = state.selectedPlayers + player)
                    } else {
                        state.copy(allPlayers = state.allPlayers + player)
                    }
                },
            )
        }
    }
}
