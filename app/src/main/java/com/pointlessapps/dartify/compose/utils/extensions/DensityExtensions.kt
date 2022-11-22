package com.pointlessapps.dartify.compose.utils.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp

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
