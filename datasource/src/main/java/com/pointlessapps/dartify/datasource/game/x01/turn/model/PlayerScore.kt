package com.pointlessapps.dartify.datasource.game.x01.turn.model

data class PlayerScore(
    val numberOfWonSets: Int,
    val numberOfWonLegs: Int,
    val doublePercentage: Float,
    val maxScore: Int,
    val averageScore: Float,
    val numberOfDarts: Int,
    val scoreLeft: Int,
    val lastScore: Int?,
    val playerId: Long,
)
