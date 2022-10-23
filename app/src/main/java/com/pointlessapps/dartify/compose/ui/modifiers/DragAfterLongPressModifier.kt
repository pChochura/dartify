package com.pointlessapps.dartify.compose.ui.modifiers

import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput

private const val LONG_PRESS_TIME = 200L

internal fun Modifier.dragAfterLongPress(
    key1: Any? = null,
    key2: Any? = null,
    onLongPressed: (Offset) -> Unit,
    onDrag: (PointerInputChange) -> Unit,
    onCancel: () -> Unit = {},
    onEnd: () -> Unit = {},
) = pointerInput(key1, key2) {
    forEachGesture {
        awaitPointerEventScope {
            val down = awaitFirstDown(false)
            kotlin.runCatching {
                withTimeout(LONG_PRESS_TIME) {
                    do {
                        val event = awaitPointerEvent()
                    } while (
                        event.type !in listOf(
                            PointerEventType.Release,
                            PointerEventType.Move,
                            PointerEventType.Move,
                        )
                    )
                }
            }.onFailure {
                onLongPressed(down.position)
                var drag = awaitPointerEvent()
                if (drag.type == PointerEventType.Release) {
                    onCancel()

                    return@awaitPointerEventScope
                }
                do {
                    drag = awaitPointerEvent()
                    if (drag.type == PointerEventType.Move) {
                        onDrag(drag.changes.first())
                    }
                } while (drag.type != PointerEventType.Release)
                onEnd()

                return@awaitPointerEventScope
            }
            onCancel()
        }
    }
}
