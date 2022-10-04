package com.pointlessapps.dartify.compose.game.active.x01.model

import com.pointlessapps.dartify.compose.game.model.Player

internal data class PlayerScore(val player: Player) {

    private val pastInputs: MutableList<Int> = mutableListOf()

    val doublePercentage: Float get() = pastInputs.count { it.isDouble() } / pastInputs.size.toFloat()
    val max: Int get() = pastInputs.maxOrNull() ?: 0
    val average: Float get() = if (pastInputs.isEmpty()) 0f else pastInputs.average().toFloat()
    val numberOfDarts: Int get() = pastInputs.size
    val score: Int get() = pastInputs.sum()

    fun addInputs(vararg scores: Int) {
        pastInputs.addAll(scores.toList())
    }

    fun popInputs(amount: Int) =
        (0..amount).reduce { acc, _ -> acc + (pastInputs.removeLastOrNull() ?: 0) }

    @Suppress("MagicNumber")
    private fun Int.isDouble() = this in (1..20).map { it * 2 } || this == 50
}
