package com.pointlessapps.dartify.local.datasource.game.x01.turn.model

import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore

internal data class Input(
    val score: InputScore,
    val numberOfThrows: Int,
    val numberOfThrowsOnDouble: Int,
)
