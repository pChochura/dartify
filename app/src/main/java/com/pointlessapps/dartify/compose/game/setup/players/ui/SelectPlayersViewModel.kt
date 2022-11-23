package com.pointlessapps.dartify.compose.game.setup.players.ui

import android.os.Parcelable
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.mappers.fromPlayer
import com.pointlessapps.dartify.compose.game.mappers.toPlayer
import com.pointlessapps.dartify.compose.game.model.Player
import com.pointlessapps.dartify.compose.utils.extensions.swapped
import com.pointlessapps.dartify.compose.utils.mutableStateOf
import com.pointlessapps.dartify.domain.database.players.usecase.DeletePlayerUseCase
import com.pointlessapps.dartify.domain.database.players.usecase.GetAllPlayersUseCase
import com.pointlessapps.dartify.domain.database.players.usecase.SavePlayerUseCase
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import com.pointlessapps.dartify.reorderable.list.ItemInfo
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import kotlin.math.sign

internal sealed interface SelectPlayersEvent {
    @JvmInline
    value class OnPlayersSelected(val players: List<Player>) : SelectPlayersEvent
    data class AskForCpuAverage(val bot: Player?) : SelectPlayersEvent
    data class AskForPlayerName(val player: Player?) : SelectPlayersEvent

    data class ShowSnackbar(
        @StringRes val message: Int,
        @StringRes val actionLabel: Int? = null,
        val actionCallback: (() -> Unit)? = null,
        val dismissCallback: (() -> Unit)? = null,
    ) : SelectPlayersEvent
}

@Immutable
@Parcelize
internal data class SelectPlayersState(
    val isLoading: Boolean = true,
    val players: List<Player> = emptyList(),
    val selectedPlayersIndex: Int = 0,
) : Parcelable

internal class SelectPlayersViewModel(
    savedStateHandle: SavedStateHandle,
    private val vibrateUseCase: VibrateUseCase,
    private val savePlayerUseCase: SavePlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val getAllPlayersUseCase: GetAllPlayersUseCase,
) : ViewModel() {

    var state by savedStateHandle.mutableStateOf(SelectPlayersState())
        private set

    private val removePlayersJobs = mutableMapOf<Long, Deferred<*>>()

    private val eventChannel = Channel<SelectPlayersEvent>()
    val events = eventChannel.receiveAsFlow()

    fun refreshPlayers() {
        getAllPlayersUseCase()
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach { players ->
                state = state.copy(
                    isLoading = false,
                    players = (state.players + players.map {
                        it.fromPlayer(ignoreDefaultOutMode = true)
                    }).distinctBy { it.id },
                )
            }
            .catch {
                eventChannel.send(SelectPlayersEvent.ShowSnackbar(R.string.something_went_wrong))
                state = state.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

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
        val isNewlyAdded = state.players.find { it.id == player.id } == null
        savePlayerUseCase(player.toPlayer())
            .take(1)
            .onStart {
                state = state.copy(isLoading = true)
            }
            .onEach {
                state = state.copy(
                    isLoading = false,
                    selectedPlayersIndex = state.selectedPlayersIndex + if (isNewlyAdded) 1 else 0,
                )
            }
            .catch {
                eventChannel.send(SelectPlayersEvent.ShowSnackbar(R.string.something_went_wrong))
                state = state.copy(isLoading = false)
            }
            .launchIn(viewModelScope)
    }

    fun onPlayerRemoved(player: Player) {
        val index = state.players.indexOf(player)
        val isSelected = index < state.selectedPlayersIndex
        val removePlayerJob = viewModelScope.async(start = CoroutineStart.LAZY) {
            deletePlayerUseCase(player.toPlayer())
                .take(1)
                .onEach {
                    state = state.copy(
                        selectedPlayersIndex = state.selectedPlayersIndex - if (isSelected) 1 else 0,
                    )
                }
                .catch {
                    eventChannel.send(SelectPlayersEvent.ShowSnackbar(R.string.something_went_wrong))
                }
                .launchIn(viewModelScope)
        }
        removePlayersJobs[player.id] = removePlayerJob
        state = state.copy(players = state.players.withMarkedAsDeleted(player.id))
        vibrateUseCase.error()

        viewModelScope.launch {
            eventChannel.send(
                SelectPlayersEvent.ShowSnackbar(
                    message = R.string.player_has_been_removed,
                    actionLabel = R.string.undo,
                    actionCallback = {
                        removePlayersJobs.remove(player.id)?.cancel()
                        state = state.copy(
                            players = state.players.withMarkedAsDeleted(player.id, false),
                        )
                        vibrateUseCase.click()

                    },
                    dismissCallback = {
                        removePlayersJobs.remove(player.id)?.start()
                    },
                ),
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

    private fun List<Player>.withMarkedAsDeleted(id: Long, deleted: Boolean = true) = map {
        if (it.id == id) {
            it.copy(deleted = deleted)
        } else {
            it
        }
    }
}
