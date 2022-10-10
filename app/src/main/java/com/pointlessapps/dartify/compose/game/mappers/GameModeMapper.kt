package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.compose.game.model.GameMode
import com.pointlessapps.dartify.domain.game.x01.model.OutMode

internal fun GameMode?.toOutMode() = when (this) {
    GameMode.Straight -> OutMode.Straight
    GameMode.Double -> OutMode.Double
    GameMode.Master -> OutMode.Master
    else -> OutMode.DEFAULT_OUT_MODE
}
