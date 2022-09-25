package com.pointlessapps.dartify.compose.utils

import androidx.compose.ui.Modifier

internal fun Modifier.conditional(
    predicate: Boolean,
    then: Modifier.() -> Modifier,
) = if (predicate) {
    then()
} else {
    this
}
