package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.compose.game.model.GameType
import com.pointlessapps.dartify.domain.database.game.model.ActiveGame

internal fun GameType.toActiveGameType() = when (this) {
    GameType.X01 -> ActiveGame.Type.X01
}
