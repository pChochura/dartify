package com.pointlessapps.dartify.local.datasource.database.players.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.dartify.local.datasource.database.players.models.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg players: PlayerEntity)

    @Query("DELETE FROM players WHERE id in (:playersIds)")
    suspend fun delete(vararg playersIds: Long)

    @Query("SELECT * FROM players")
    fun get(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun get(id: Long): Flow<List<PlayerEntity>>
}
