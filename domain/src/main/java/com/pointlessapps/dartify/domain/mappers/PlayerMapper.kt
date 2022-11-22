package com.pointlessapps.dartify.domain.mappers

import com.pointlessapps.dartify.datasource.database.players.model.Player
import com.pointlessapps.dartify.domain.model.Player as RemotePlayer

internal fun Player.toPlayer() = RemotePlayer(
    id = id,
    name = name,
    outMode = outMode.toGameMode(),
    average = average,
)

internal fun RemotePlayer.fromPlayer() = Player(
    id = id,
    name = name,
    outMode = outMode.fromGameMode(),
    average = average,
)
