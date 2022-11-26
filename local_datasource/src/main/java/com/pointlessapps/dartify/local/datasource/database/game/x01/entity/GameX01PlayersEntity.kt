package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.pointlessapps.dartify.datasource.database.model.GameMode

@Entity(tableName = "x01_game_players", primaryKeys = ["game_id", "player_id"])
internal data class GameX01PlayersEntity(
    @ColumnInfo(name = "game_id", index = true)
    val gameId: Long,
    @ColumnInfo(name = "player_id", index = true)
    val playerId: Long,
    @ColumnInfo(name = "out_mode")
    val outMode: GameMode,
)
