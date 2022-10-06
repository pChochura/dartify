package com.pointlessapps.dartify.compose.ui.theme

import android.os.Parcelable
import com.pointlessapps.dartify.compose.game.model.GameSettings
import kotlinx.parcelize.Parcelize

internal sealed interface Route : Parcelable {

    @Parcelize
    object Home : Route

    @Parcelize
    object Players : Route

    object GameSetup {
        @Parcelize
        object X01 : Route
    }

    object GameActive {
        @Parcelize
        data class X01(val gameSettings: GameSettings) : Route
    }
}
