package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.pointlessapps.dartify.datasource.database.model.GameMode
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

@Entity(
    tableName = "x01_game_players",
    primaryKeys = ["game_id", "player_id"],
    foreignKeys = [
        ForeignKey(
            entity = GameX01Entity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
)
internal data class GameX01PlayersEntity(
    @ColumnInfo(name = "game_id", index = true)
    val gameId: Long,
    @ColumnInfo(name = "player_id", index = true)
    val playerId: Long,
    @ColumnInfo(name = "out_mode")
    val outMode: GameMode,
    @ColumnInfo(name = "order")
    val order: Int,
)
