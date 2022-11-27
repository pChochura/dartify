package com.pointlessapps.dartify.local.datasource.database.game.mappers

import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame
import com.pointlessapps.dartify.local.datasource.database.game.entity.ActiveGameEntity

internal fun ActiveGame.toActiveGameEntity() = ActiveGameEntity(
    gameId = gameId,
    title = title,
    subtitle = subtitle,
    type = type,
)

internal fun ActiveGameEntity.toActiveGame() = ActiveGame(
    gameId = gameId,
    title = title,
    subtitle = subtitle,
    type = type,
)
