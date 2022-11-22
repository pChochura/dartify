package com.pointlessapps.dartify.datasource.database.players.model

data class Player(
    val id: Long,
    val name: String,
    val outMode: GameMode,
    val average: Float?,
)
