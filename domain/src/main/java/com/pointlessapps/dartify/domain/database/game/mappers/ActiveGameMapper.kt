package com.pointlessapps.dartify.domain.database.game.mappers

import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.datasource.database.model.GameMode
import com.pointlessapps.dartify.domain.database.game.model.ActiveGame as RemoteActiveGame

internal fun GameX01.toActiveGame(gameId: Long) = ActiveGame(
    gameId = gameId,
    title = startingScore.toString(),
    subtitle = "${inMode.toAbbrev()}-IN",
    type = ActiveGame.Type.X01,
)

private fun GameMode.toAbbrev() = when (this) {
    GameMode.Straight -> "S"
    GameMode.Double -> "D"
    GameMode.Master -> "M"
}

internal fun ActiveGame.toActiveGame() = RemoteActiveGame(
    gameId = gameId,
    title = title,
    subtitle = subtitle,
    type = type.toActiveGameType(),
)

private fun ActiveGame.Type.toActiveGameType() = when (this) {
    ActiveGame.Type.X01 -> RemoteActiveGame.Type.X01
}
