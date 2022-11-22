package com.pointlessapps.dartify.compose.game.setup.players.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.utils.extensions.swapped
import com.pointlessapps.dartify.compose.utils.extensions.withInsertedAt
import com.pointlessapps.dartify.compose.utils.extensions.withReplacedOrInserted
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import com.pointlessapps.dartify.reorderable.list.ItemInfo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.math.sign

internal sealed interface SelectPlayersEvent {
    @JvmInline
    value class OnPlayersSelected(val players: List<Player>) : SelectPlayersEvent
    data class AskForCpuAverage(val bot: Player?) : SelectPlayersEvent
    data class AskForPlayerName(val player: Player?) : SelectPlayersEvent

    data class ShowActionSnackbar(
        @StringRes val message: Int,
        @StringRes val actionLabel: Int,
        val actionCallback: () -> Unit,
    ) : SelectPlayersEvent
}

internal data class SelectPlayersState(
    val players: List<Player> = emptyList(),
    val selectedPlayersIndex: Int = 0,
)

internal class SelectPlayersViewModel(
    private val vibrateUseCase: VibrateUseCase,
) : ViewModel() {

    var state by mutableStateOf(SelectPlayersState())
        private set

    private val eventChannel = Channel<SelectPlayersEvent>()
    val events = eventChannel.receiveAsFlow()

    fun setSelectedPlayers(selectedPlayers: List<Player>) {
        state = state.copy(
            players = selectedPlayers,
            selectedPlayersIndex = if (selectedPlayers.isEmpty()) 0 else selectedPlayers.size,
        )
    }

    fun onSaveClicked() {
        viewModelScope.launch {
            eventChannel.send(
                SelectPlayersEvent.OnPlayersSelected(
                    state.players.subList(0, state.selectedPlayersIndex),
                ),
            )
        }
    }

    fun onPlayerClicked(player: Player) {
        vibrateUseCase.click()
        viewModelScope.launch {
            eventChannel.send(
                if (player.botOptions != null) {
                    SelectPlayersEvent.AskForCpuAverage(player)
                } else {
                    SelectPlayersEvent.AskForPlayerName(player)
                },
            )
        }
    }

    fun onAddCpuClicked() {
        viewModelScope.launch {
            eventChannel.send(SelectPlayersEvent.AskForCpuAverage(null))
        }
    }

    fun onAddPlayerClicked() {
        viewModelScope.launch {
            eventChannel.send(SelectPlayersEvent.AskForPlayerName(null))
        }
    }

    fun onPlayerAdded(player: Player) {
        val newlyAdded = state.players.find { it.id == player.id } == null
        state = state.copy(
            players = state.players.withReplacedOrInserted({ it.id == player.id }, player),
            selectedPlayersIndex = state.selectedPlayersIndex + if (newlyAdded) 1 else 0,
        )
    }

    fun onPlayerRemoved(player: Player) {
        val index = state.players.indexOf(player)
        val isSelected = index < state.selectedPlayersIndex
        state = state.copy(
            players = (state.players - player),
            selectedPlayersIndex = state.selectedPlayersIndex - if (isSelected) 1 else 0,
        )

        vibrateUseCase.error()
        viewModelScope.launch {
            eventChannel.send(
                SelectPlayersEvent.ShowActionSnackbar(
                    R.string.player_has_been_removed,
                    R.string.undo,
                ) {
                    vibrateUseCase.click()
                    if (state.players.isEmpty()) {
                        state = state.copy(
                            players = listOf(player),
                            selectedPlayersIndex = if (isSelected) 1 else 0,
                        )
                        return@ShowActionSnackbar
                    }

                    val currentIndex = index.coerceIn(0, state.players.lastIndex)
                    state = state.copy(
                        players = state.players.withInsertedAt(currentIndex, player),
                        selectedPlayersIndex = state.selectedPlayersIndex + if (isSelected) 1 else 0,
                    )
                },
            )
        }
    }

    fun onPlayersSwapped(from: ItemInfo, to: ItemInfo) {
        vibrateUseCase.tick()

        if (to.key == R.string.label_all) {
            val direction = (from.index - to.index).sign
            state = state.copy(
                selectedPlayersIndex = state.selectedPlayersIndex + direction,
            )

            return
        }

        val fromIndex = state.players.indexById(from.key) ?: return
        val toIndex = state.players.indexById(to.key) ?: return

        state = state.copy(
            players = state.players.swapped(fromIndex, toIndex),
        )
    }

    fun onDragStarted() {
        vibrateUseCase.click()
    }

    private fun List<Player>.indexById(id: Any?) = indexOfFirst { it.id == id }
        .takeIf { it != -1 }
}
