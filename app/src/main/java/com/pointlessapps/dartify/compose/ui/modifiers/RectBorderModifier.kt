package com.pointlessapps.dartify.compose.ui.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

internal fun Modifier.rectBorder(
    top: Dp? = null,
    bottom: Dp? = null,
    left: Dp? = null,
    right: Dp? = null,
    color: Color,
): Modifier = composed {
    val density = LocalDensity.current
    drawWithCache {
        onDrawWithContent {
            drawContent()
            with(density) {
                top?.let { drawTopBorder(top.toPx(), color) }
                bottom?.let { drawBottomBorder(bottom.toPx(), color) }
                left?.let { drawLeftBorder(left.toPx(), color) }
                right?.let { drawRightBorder(right.toPx(), color) }
            }
        }
    }
}

internal fun ContentDrawScope.drawTopBorder(width: Float, color: Color) =
    drawLine(color, Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = width)

internal fun ContentDrawScope.drawBottomBorder(width: Float, color: Color) =
    drawLine(
        color,
        Offset(0f, size.height),
        Offset(size.width, size.height),
        strokeWidth = width,
    )

internal fun ContentDrawScope.drawLeftBorder(width: Float, color: Color) =
    drawLine(color, Offset(0f, 0f), Offset(0f, size.height), strokeWidth = width)

internal fun ContentDrawScope.drawRightBorder(width: Float, color: Color) =
    drawLine(
        color,
        Offset(size.width, 0f),
        Offset(size.width, size.height),
        strokeWidth = width,
    )
