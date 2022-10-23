package com.pointlessapps.dartify.compose.ui.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

internal class AbovePositionProvider(private val anchor: Offset) :
    PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        var popupPosition = IntOffset(anchorBounds.left, anchorBounds.top)
        popupPosition += IntOffset(anchor.x.toInt(), anchor.y.toInt())
        popupPosition -= IntOffset(popupContentSize.width / 4, popupContentSize.height)
        return popupPosition
    }
}
