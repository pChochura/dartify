package com.pointlessapps.dartify.datasource.database.players.model

import com.pointlessapps.dartify.datasource.database.model.GameMode

data class Player(
    val id: Long,
    val name: String,
    val outMode: GameMode,
    val average: Float?,
)
