package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

internal class GameX01WithPlayersEntity {
    @Embedded
    lateinit var game: GameX01Entity

    @Relation(
        entity = PlayerEntity::class,
        parentColumn = "current_player_id",
        entityColumn = "id",
    )
    lateinit var currentPlayer: PlayerEntity

    @Relation(
        entity = GameX01InputEntity::class,
        parentColumn = "id",
        entityColumn = "game_id",
    )
    lateinit var inputs: List<GameX01InputWithPlayerEntity>

    @Relation(
        entity = PlayerEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GameX01PlayersEntity::class,
            parentColumn = "game_id",
            entityColumn = "player_id",
        ),
    )
    lateinit var players: List<PlayerEntity>
}
