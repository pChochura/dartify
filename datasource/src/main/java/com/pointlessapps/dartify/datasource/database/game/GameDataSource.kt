package com.pointlessapps.dartify.datasource.database.game

import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame

interface GameDataSource {
    /**
     * Writes a game data into the database. Returns the id of the created entry
     */
    suspend fun insertActiveGame(game: ActiveGame): Long

    /**
     * Returns all saved active game objects
     */
    suspend fun getAllActiveGames(): List<ActiveGame>

    /**
     * Removes the active game from the database
     */
    suspend fun deleteActiveGame(gameId: Long)
}
