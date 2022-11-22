package com.pointlessapps.dartify.domain.game.x01.model

import com.pointlessapps.dartify.domain.model.Player

data class PlayerScore(
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
