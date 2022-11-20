package com.pointlessapps.dartify.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
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

/**
 * Replace items that satisfy the [predicate] with the provided [value].
 * If none of the values satisfies the [predicate], the [value] will be inserted at the beginning
 * of the list
 */
internal fun <T> ImmutableList<T>.withReplacedOrInserted(
    predicate: (T) -> Boolean,
    value: T,
): ImmutableList<T> {
    var replaced = false
    val result = MutableList(this.size) {
        if (predicate(get(it))) {
            replaced = true
            value
        } else {
            get(it)
        }
    }

    if (!replaced) {
        result.add(0, value)
    }

    return result.toImmutableList()
}

/**
 * Insert the [value] at the [index] position and return the list back
 */
internal fun <T> ImmutableList<T>.withInsertedAt(index: Int, value: T) =
    toMutableList().apply { add(index, value) }.toImmutableList()

internal fun <T> ImmutableList<T>.swapped(from: Int, to: Int): ImmutableList<T> {
    val fromItem = get(from)
    val toItem = get(to)

    return mapIndexed { index, item ->
        return@mapIndexed when (index) {
            from -> toItem
            to -> fromItem
            else -> item
        }
    }.toImmutableList()
}
