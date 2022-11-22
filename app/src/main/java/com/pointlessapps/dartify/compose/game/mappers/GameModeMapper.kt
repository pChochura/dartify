package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.domain.model.GameMode
import com.pointlessapps.dartify.compose.game.model.GameMode as ViewGameMode

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

internal fun GameMode.fromOutMode(ignoreDefault: Boolean): ViewGameMode? {
    if (ignoreDefault && this == GameMode.DEFAULT_OUT_MODE) {
        return null
    }

    return when (this) {
        GameMode.Straight -> ViewGameMode.Straight
        GameMode.Double -> ViewGameMode.Double
        GameMode.Master -> ViewGameMode.Master
    }
}
