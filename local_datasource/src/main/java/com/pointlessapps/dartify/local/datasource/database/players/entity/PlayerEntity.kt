package com.pointlessapps.dartify.local.datasource.database.players.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
internal data class PlayerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "average")
    val average: Float?,
)
