package com.pointlessapps.dartify.local.datasource.database.players.mappers

import com.pointlessapps.dartify.datasource.database.players.model.Player
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

internal fun Player.toPlayerEntity() = PlayerEntity(
    id = id,
    name = name,
    average = average,
    outMode = outMode,
)

internal fun PlayerEntity.toPlayer() = Player(
    id = id,
    name = name,
    average = average,
    outMode = outMode,
)
