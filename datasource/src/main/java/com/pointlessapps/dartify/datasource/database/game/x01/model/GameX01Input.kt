package com.pointlessapps.dartify.datasource.database.game.x01.model

import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore

data class GameX01Input(
    val playerId: Long,
    val score: InputScore,
    val numberOfThrows: Int,
    val numberOfThrowsOnDouble: Int,
    val type: Type,
    val won: Boolean,
) {
    enum class Type {
        LEG_FINISHED, SET_FINISHED, CURRENT_LEG
    }
}
