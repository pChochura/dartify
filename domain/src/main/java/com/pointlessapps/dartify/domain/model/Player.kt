package com.pointlessapps.dartify.domain.model

data class Player(
    val id: Long,
    val name: String,
    val outMode: GameMode,
    val average: Float? = null,
)
