package com.pointlessapps.dartify.datasource.database.game.x01.model

import com.pointlessapps.dartify.datasource.game.x01.turn.model.InputScore

data class GameX01Input(
    val playerId: Long,
    val score: InputScore,
    val numberOfThrows: Int,
    val numberOfThrowsOnDouble: Int,
    val won: Boolean,
    val legIndex: Int,
    val setIndex: Int,
    val order: Int,
)
