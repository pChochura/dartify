package com.pointlessapps.dartify.compose.ui.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface Route : Parcelable {
    @Parcelize
    object Home : Route

    object GameSetup {
        @Parcelize
        object X01 : Route
    }

    object GameActive {
        @Parcelize
        object X01 : Route
    }
}
