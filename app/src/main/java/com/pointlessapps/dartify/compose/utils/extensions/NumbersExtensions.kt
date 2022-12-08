package com.pointlessapps.dartify.compose.utils.extensions

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

internal fun Float.toPercentage() = (this * 100).toInt()

internal fun Int.addDecimal(decimal: Int) = this * 10 + decimal

internal fun Int.removeDecimal() = this / 10

internal fun Offset.distance(offset: Offset): Float = sqrt(
    (offset.x - x) * (offset.x - x) + (offset.y - y) * (offset.y - y),
)
