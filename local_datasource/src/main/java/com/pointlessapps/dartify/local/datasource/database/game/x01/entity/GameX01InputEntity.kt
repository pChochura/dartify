package com.pointlessapps.dartify.local.datasource.database.game.x01.entity

import androidx.room.*
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity

@Entity(
    tableName = "x01_game_inputs",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.NO_ACTION,
        ),
        ForeignKey(
            entity = GameX01Entity::class,
            parentColumns = ["id"],
            childColumns = ["game_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
internal data class GameX01InputEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "game_id", index = true)
    val gameId: Long,
    @ColumnInfo(name = "player_id", index = true)
    val playerId: Long,
    @ColumnInfo(name = "score")
    val score: Int,
    @ColumnInfo(name = "number_of_throws")
    val numberOfThrows: Int,
    @ColumnInfo(name = "number_of_throws_on_double")
    val numberOfThrowsOnDouble: Int,
    @ColumnInfo(name = "type")
    val type: GameX01Input.Type,
    @ColumnInfo(name = "won")
    val won: Boolean,
    @ColumnInfo(name = "leg_index")
    val legIndex: Int,
    @ColumnInfo(name = "order")
    val order: Int,
)
