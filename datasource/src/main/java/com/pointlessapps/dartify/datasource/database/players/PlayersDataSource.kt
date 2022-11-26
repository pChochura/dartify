package com.pointlessapps.dartify.datasource.database.players

import com.pointlessapps.dartify.datasource.database.players.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayersDataSource {
    /**
     * Writes a player data into the database. If the player with a [player.id] already exists, it
     * will be replaced
     */
    suspend fun insertPlayer(player: Player)

    /**
     * Deletes a player with a given [player.id] from the database
     */
    suspend fun deletePlayer(player: Player)

    /**
     * Returns all stored players in the database
     */
    fun getPlayers(): Flow<List<Player>>
}
