package com.pointlessapps.dartify.domain.database.game.model

data class ActiveGame(
    val gameId: Long,
    val title: String,
    val subtitle: String,
    val type: Type,
) {
    enum class Type { X01 }
}
