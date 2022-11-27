package com.pointlessapps.dartify.local.datasource.database.players.mappers

import com.pointlessapps.dartify.datasource.database.model.GameMode
import com.pointlessapps.dartify.datasource.database.players.model.Player
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

internal fun Player.toPlayerEntity() = PlayerEntity(
    id = id,
    name = name,
    average = average,
)

internal fun PlayerEntity.toPlayer(outMode: GameMode = GameMode.DEFAULT_OUT_MODE) = Player(
    id = id,
    name = name,
    average = average,
    outMode = outMode,
)
