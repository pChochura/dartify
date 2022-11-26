package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

internal class GameX01InputWithPlayerEntity {
    @Embedded
    lateinit var input: GameX01InputEntity

    @Relation(
        entity = PlayerEntity::class,
        parentColumn = "player_id",
        entityColumn = "id",
    )
    lateinit var player: PlayerEntity
}
