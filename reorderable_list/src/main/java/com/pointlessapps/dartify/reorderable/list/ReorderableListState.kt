/*
    Thanks to https://gist.github.com/surajsau/f5342f443352195208029e98b0ee39f3
*/

package com.pointlessapps.dartify.reorderable.list

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun rememberReorderableListState(
    lazyListState: LazyListState = rememberLazyListState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    onMove: (ItemInfo, ItemInfo) -> Unit,
    onDragStarted: (() -> Unit)? = null,
) = remember {
    ReorderableListState(
        lazyListState = lazyListState,
        coroutineScope = coroutineScope,
        onMove = onMove,
        onDragStarted = onDragStarted,
    )
}

class ReorderableListState internal constructor(
    val lazyListState: LazyListState,
    private val coroutineScope: CoroutineScope,
    private val onMove: (ItemInfo, ItemInfo) -> Unit,
    private val onDragStarted: (() -> Unit)? = null,
) {
    internal val reorderableAnchorKeys = mutableStateListOf<Any?>()
    internal val reorderableKeys = mutableStateListOf<Any?>()
    internal var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    internal var previousIndexOfDraggedItem by mutableStateOf<Int?>(null)

    private var overscrollJob by mutableStateOf<Job?>(null)
    private var draggedDistance by mutableStateOf(0f)
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)

    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let { it.offset to it.offsetEnd }

    internal val itemOffsetAnimation = Animatable(0f)
    internal val elementDisplacement: Float?
        get() = currentElement?.let { item ->
            (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
        }

    private val currentElement: LazyListItemInfo?
        get() = lazyListState.getVisibleItemInfoFor(currentIndexOfDraggedItem)

    fun isDragged(key: Any?) = currentElement?.key == key

    internal fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .filter { it.key in reorderableKeys && it.key !in reorderableAnchorKeys }
            .firstOrNull { offset.y.toInt() in it.offset..(it.offset + it.size) }
            ?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it

                onDragStarted?.invoke()
            }
    }

    internal fun onDragInterrupted() {
        elementDisplacement?.let {
            previousIndexOfDraggedItem = currentIndexOfDraggedItem
            coroutineScope.launch {
                itemOffsetAnimation.snapTo(it)
                itemOffsetAnimation.animateTo(0f)
            }
        }

        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overscrollJob?.cancel()
    }

    internal fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { it.offsetEnd < startOffset || it.offset > endOffset || hovered.index == it.index }
                    .filter { it.key in reorderableKeys }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }
                    ?.also { item ->
                        lazyListState.getVisibleItemInfoFor(currentIndexOfDraggedItem)?.let {
                            onMove(ItemInfo(it.index, it.key), ItemInfo(item.index, item.key))
                            currentIndexOfDraggedItem = item.index
                        }
                    }
            }
        }
    }

    internal fun checkForOverScroll() = initiallyDraggedElement?.let {
        val startOffset = it.offset + draggedDistance
        val endOffset = it.offsetEnd + draggedDistance

        return@let when {
            draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset)
                .takeIf { diff -> diff > 0 }
            draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset)
                .takeIf { diff -> diff < 0 }
            else -> null
        }
    } ?: 0f
}