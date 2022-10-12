package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.compose.game.model.GameMode as ViewGameMode
import com.pointlessapps.dartify.domain.game.x01.model.GameMode

internal fun ViewGameMode?.toOutMode() = when (this) {
    ViewGameMode.Straight -> GameMode.Straight
    ViewGameMode.Double -> GameMode.Double
    ViewGameMode.Master -> GameMode.Master
    else -> GameMode.DEFAULT_OUT_MODE
}

internal fun ViewGameMode?.toInMode() = when (this) {
    ViewGameMode.Straight -> GameMode.Straight
    ViewGameMode.Double -> GameMode.Double
    ViewGameMode.Master -> GameMode.Master
    else -> GameMode.DEFAULT_IN_MODE
}
