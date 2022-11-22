package com.pointlessapps.dartify.compose.ui.theme

import android.os.Parcelable
import androidx.compose.runtime.MutableState
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

internal sealed interface Route : Parcelable {

    @Parcelize
    object Home : Route

    @Parcelize
    data class Players(val selectedPlayers: List<Player>) : Route {
        interface PlayersListCallback {
            val players: MutableState<List<Player>>
        }
    }

    object GameSetup {
        @Parcelize
        data class X01(
            override val players: @RawValue MutableState<List<Player>>,
        ) : Route, Players.PlayersListCallback
    }

    object GameActive {
        @Parcelize
        data class X01(val gameSettings: GameSettings) : Route
    }
}
