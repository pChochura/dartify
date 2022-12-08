package com.pointlessapps.dartify.local.datasource.database.game.daos

import androidx.room.*
import com.pointlessapps.dartify.local.datasource.database.game.entity.ActiveGameEntity

@Dao
internal interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(game: ActiveGameEntity): Long

    @Query("DELETE FROM active_games WHERE game_id in (:gameIds)")
    suspend fun delete(vararg gameIds: Long)

    @Query("SELECT * FROM active_games")
    suspend fun get(): List<ActiveGameEntity>
}
