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
    suspend fun insertGamePlayers(game: List<GameX01PlayersEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameInputs(inputs: List<GameX01InputEntity>)

    @Transaction
    @Query("SELECT * FROM x01_games, x01_game_inputs, x01_game_players " +
            "ORDER BY x01_game_players.player_id, x01_game_inputs.`order`")
    suspend fun get(): List<GameX01WithPlayersEntity>

    @Transaction
    @Query("SELECT * FROM x01_games WHERE id = :gameId")
    suspend fun get(gameId: Long): GameX01WithPlayersEntity

    @Query("SELECT * FROM x01_game_players WHERE game_id = :gameId ORDER BY `order`")
    suspend fun getGamePlayers(gameId: Long): List<GameX01PlayersEntity>

    @Query("DELETE FROM x01_games WHERE id = :gameId")
    suspend fun delete(gameId: Long)
}
