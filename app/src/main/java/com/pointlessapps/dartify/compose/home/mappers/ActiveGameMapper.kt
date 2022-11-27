package com.pointlessapps.dartify.compose.home.mappers

import com.pointlessapps.dartify.compose.game.model.GameType
import com.pointlessapps.dartify.domain.database.game.model.ActiveGame
import com.pointlessapps.dartify.compose.home.model.ActiveGame as ViewActiveGame

internal fun ActiveGame.fromActiveGame() = ViewActiveGame(
    gameId = gameId,
    title = title,
    subtitle = subtitle,
    type = when (type) {
        ActiveGame.Type.X01 -> GameType.X01
    },
)
