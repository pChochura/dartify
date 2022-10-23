package com.pointlessapps.dartify.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

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

@Suppress("MagicNumber")
internal fun Float.toPercentage() = (this * 100).toInt()

@Suppress("MagicNumber")
internal fun Int.addDecimal(decimal: Int) = this * 10 + decimal

internal fun Offset.distance(offset: Offset): Float = sqrt(
    (offset.x - x) * (offset.x - x) + (offset.y - y) * (offset.y - y),
)
