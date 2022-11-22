package com.pointlessapps.dartify.domain.mappers

import com.pointlessapps.dartify.datasource.database.players.model.GameMode
import com.pointlessapps.dartify.domain.model.GameMode as RemoteGameMode

internal fun GameMode.toGameMode() = when (this) {
    GameMode.Straight -> RemoteGameMode.Straight
    GameMode.Double -> RemoteGameMode.Double
    GameMode.Master -> RemoteGameMode.Master
}

internal fun RemoteGameMode.fromGameMode() = when (this) {
    RemoteGameMode.Straight -> GameMode.Straight
    RemoteGameMode.Double -> GameMode.Double
    RemoteGameMode.Master -> GameMode.Master
}
