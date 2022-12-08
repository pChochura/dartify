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

    @RewriteQueriesToDropUnusedColumns
    @Transaction
    @Query("SELECT * FROM x01_games, x01_game_players WHERE x01_games.id = x01_game_players.game_id " +
            "AND x01_game_players.player_id = :playerId")
    suspend fun getForPlayer(playerId: Long): List<GameX01WithPlayersEntity>

    @Query("SELECT * FROM x01_game_players WHERE game_id in (:gameIds) ORDER BY `order`")
    suspend fun getGamesPlayers(vararg gameIds: Long): List<GameX01PlayersEntity>

    @Query("SELECT * FROM x01_game_players WHERE player_id = :playerId")
    suspend fun getGamePlayer(playerId: Long): GameX01PlayersEntity

    @Query("DELETE FROM x01_games WHERE id in (:gameIds)")
    suspend fun delete(vararg gameIds: Long)

    @Query("DELETE FROM x01_game_players WHERE player_id = :playerId")
    suspend fun deleteGamePlayer(playerId: Long)

    @Query("DELETE FROM x01_game_inputs WHERE player_id = :playerId")
    suspend fun deleteGameInputs(playerId: Long)
}
