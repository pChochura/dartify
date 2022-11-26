package com.pointlessapps.dartify.local.datasource.database.game.x01.daos

import androidx.room.*
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01Entity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01InputEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01PlayersEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01WithPlayersEntity

@Dao
internal interface GameX01Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: GameX01Entity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGamePlayerCrossRefs(game: List<GameX01PlayersEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameInputs(inputs: List<GameX01InputEntity>)

    @Transaction
    @Query("SELECT * FROM x01_games")
    suspend fun get(): List<GameX01WithPlayersEntity>
}
