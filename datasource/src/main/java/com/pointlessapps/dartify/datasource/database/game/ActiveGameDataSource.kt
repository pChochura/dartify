package com.pointlessapps.dartify.datasource.database.game

import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame

interface ActiveGameDataSource {
    /**
     * Writes a game data into the database. Returns the id of the created entry
     */
    suspend fun insertActiveGame(game: ActiveGame): Long

    /**
     * Returns all saved active game objects
     */
    suspend fun getAllActiveGames(): List<ActiveGame>

    /**
     * Removes active games with the given [gameIds] from the database
     */
    suspend fun deleteActiveGames(vararg gameIds: Long)
}
