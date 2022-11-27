package com.pointlessapps.dartify.reorderable.list

import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Marks a LazyList as a container for the reorderable items
 */
fun Modifier.reorderable(reorderableListState: ReorderableListState) = composed {
    val scope = rememberCoroutineScope()
    var overscrollJob by remember { mutableStateOf<Job?>(null) }

    pointerInput(Unit) {
        detectDragGesturesAfterLongPress(
            onDrag = { change, offset ->
                change.consume()
                reorderableListState.onDrag(offset)

                if (overscrollJob?.isActive == true) {
                    return@detectDragGesturesAfterLongPress
                }

                reorderableListState.checkForOverScroll()
                    .takeIf { it != 0f }
                    ?.let {
                        overscrollJob = scope.launch {
                            reorderableListState.lazyListState.scrollBy(it)
                        }
                    } ?: run { overscrollJob?.cancel() }
            },
            onDragStart = { reorderableListState.onDragStart(it) },
            onDragEnd = { reorderableListState.onDragInterrupted() },
            onDragCancel = { reorderableListState.onDragInterrupted() },
        )
    }
}

/**
 * Marks an item as a possible target of reorder action. Reordering can be initiated from this
 * item by a long press gesture
 */
fun Modifier.reorderableItem(
    key: Any?,
    reorderableListState: ReorderableListState,
    nonDraggedModifier: Modifier = this,
) = composed {
    remember(key) { reorderableListState.reorderableKeys.add(key) }

    val index = reorderableListState.lazyListState.getVisibleItemIndexFor(key)

    val currentOffset = reorderableListState.elementDisplacement ?: 0f
    val animationOffset by reorderableListState.itemOffsetAnimation.asState()

    return@composed when {
        index == reorderableListState.currentIndexOfDraggedItem ->
            zIndex(2f).graphicsLayer { translationY = currentOffset }
        index == reorderableListState.previousIndexOfDraggedItem && animationOffset != 0f ->
            zIndex(1f).graphicsLayer { translationY = animationOffset }
        else -> nonDraggedModifier
    }
}

/**
 * Marks an item as a target of reorder action. When a another item tries to swap places with this
 * one, the onMove method will be called. This item cannot initiate reorder action.
 */
fun Modifier.reorderableAnchorItem(
    key: Any?,
    reorderableListState: ReorderableListState,
): Modifier {
    reorderableListState.reorderableKeys.add(key)
    reorderableListState.reorderableAnchorKeys.add(key)

    return this
}
