package com.pointlessapps.dartify.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp

internal inline fun Modifier.conditional(
    predicate: Boolean,
    then: Modifier.() -> Modifier,
) = if (predicate) {
    then()
} else {
    this
}

/**
 * Warning!
 * Use cautiously! Scaling back the text is strongly unadvised
 */
@Composable
internal fun Int.scaledSp() = toFloat().scaledSp()

/**
 * Warning!
 * Use cautiously! Scaling back the text is strongly unadvised
 */
@Composable
internal fun Float.scaledSp() = with(LocalDensity.current) {
    val fontScale = this.fontScale
    val textSize = this@scaledSp / fontScale
    return@with textSize.sp
}

internal fun Float.toPercentage() = (this * 100).toInt()
