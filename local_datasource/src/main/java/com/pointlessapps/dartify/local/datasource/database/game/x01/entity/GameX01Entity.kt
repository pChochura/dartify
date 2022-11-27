package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.pointlessapps.dartify.datasource.database.game.model.MatchResolutionStrategy
import com.pointlessapps.dartify.datasource.database.model.GameMode
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

@Entity(
    tableName = "x01_games",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["current_player_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.NO_ACTION,
        ),
    ],
)
internal data class GameX01Entity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "current_player_id", index = true)
    val currentPlayerId: Long,
    @ColumnInfo(name = "starting_score")
    val startingScore: Int,
    @ColumnInfo(name = "number_of_sets")
    val numberOfSets: Int,
    @ColumnInfo(name = "number_of_legs")
    val numberOfLegs: Int,
    @ColumnInfo(name = "in_mode")
    val inMode: GameMode,
    @ColumnInfo(name = "match_resolution_strategy")
    val matchResolutionStrategy: MatchResolutionStrategy,
)
