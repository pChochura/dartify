package com.pointlessapps.dartify.local.datasource.database.players.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pointlessapps.dartify.datasource.database.players.model.GameMode

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "average")
    val average: Float?,
    @ColumnInfo(name = "out_mode")
    val outMode: GameMode,
)
