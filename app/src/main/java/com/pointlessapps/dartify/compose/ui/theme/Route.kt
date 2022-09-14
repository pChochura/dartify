package com.pointlessapps.dartify.compose.ui.theme

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface Route : Parcelable {
    @Parcelize
    object Home : Route
}
