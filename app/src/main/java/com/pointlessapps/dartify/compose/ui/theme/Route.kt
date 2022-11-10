package com.pointlessapps.dartify.compose.ui.theme

import android.os.Parcelable
import com.pointlessapps.dartify.compose.game.model.GameSettings
import com.pointlessapps.dartify.compose.game.model.Player
import kotlinx.collections.immutable.ImmutableList
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

internal sealed interface Route : Parcelable {

    @Parcelize
    object Home : Route

    @Parcelize
    class Players(
        val selectedPlayers: @RawValue ImmutableList<Player>,
        val callback: (ImmutableList<Player>) -> Unit,
    ) : Route

    object GameSetup {
        @Parcelize
        object X01 : Route
    }

    object GameActive {
        @Parcelize
        data class X01(val gameSettings: GameSettings) : Route
    }
}
