package com.pointlessapps.dartify.compose.utils.extensions

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

@Suppress("MagicNumber")
internal fun Float.toPercentage() = (this * 100).toInt()

@Suppress("MagicNumber")
internal fun Int.addDecimal(decimal: Int) = this * 10 + decimal

internal fun Offset.distance(offset: Offset): Float = sqrt(
    (offset.x - x) * (offset.x - x) + (offset.y - y) * (offset.y - y),
)
