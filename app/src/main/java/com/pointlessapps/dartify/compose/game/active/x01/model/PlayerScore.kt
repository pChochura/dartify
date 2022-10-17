package com.pointlessapps.dartify.compose.game.active.x01.model

import com.pointlessapps.dartify.compose.game.model.Player

internal data class PlayerScore(
    val numberOfWonSets: Int,
    val numberOfWonLegs: Int,
    val doublePercentage: Float,
    val maxScore: Int,
    val averageScore: Float,
    val numberOfDarts: Int,
    val scoreLeft: Int,
    val lastScore: Int?,
    val player: Player,
)
