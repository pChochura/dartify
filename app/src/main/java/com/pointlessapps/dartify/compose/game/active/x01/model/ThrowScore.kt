package com.pointlessapps.dartify.compose.game.active.x01.model

import androidx.annotation.Keep

internal data class ThrowScore(
    val value: Int,
    val multiplier: Multiplier,
) {
    @Keep
    enum class Multiplier {
        Single, Double, Triple;

        override fun toString() = when (this) {
            Single -> ""
            Double -> "D"
            Triple -> "T"
        }
    }

    override fun toString() = "$multiplier$value"
}
