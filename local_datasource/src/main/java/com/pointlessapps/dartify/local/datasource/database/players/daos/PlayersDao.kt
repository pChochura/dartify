package com.pointlessapps.dartify.local.datasource.database.players.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface PlayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: PlayerEntity)

    @Query("DELETE FROM players WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM players")
    fun get(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :id")
    fun get(id: Long): Flow<List<PlayerEntity>>
}
