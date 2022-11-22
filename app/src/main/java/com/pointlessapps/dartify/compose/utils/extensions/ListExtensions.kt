package com.pointlessapps.dartify.compose.utils.extensions


/**
 * Replace items that satisfy the [predicate] with the provided [value].
 * If none of the values satisfies the [predicate], the [value] will be inserted at the beginning
 * of the list
 */
internal fun <T> List<T>.withReplacedOrInserted(
    predicate: (T) -> Boolean,
    value: T,
): List<T> {
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

    return result
}

/**
 * Insert the [value] at the [index] position and return the list back
 */
internal fun <T> List<T>.withInsertedAt(index: Int, value: T) =
    toMutableList().apply { add(index, value) }

internal fun <T> List<T>.swapped(from: Int, to: Int): List<T> {
    val fromItem = get(from)
    val toItem = get(to)

    return mapIndexed { index, item ->
        return@mapIndexed when (index) {
            from -> toItem
            to -> fromItem
            else -> item
        }
    }
}
