package com.pointlessapps.dartify.local.datasource.database.game.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame

@Entity(tableName = "active_games")
internal data class ActiveGameEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "game_id")
    val gameId: Long,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "subtitle")
    val subtitle: String,
    @ColumnInfo(name = "type")
    val type: ActiveGame.Type,
)
