package com.pointlessapps.dartify.datasource.database.game.x01

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01

interface GameX01DataSource {
    /**
     * Writes a game data into the database. Returns the id of the created entry
     */
    suspend fun insertGame(game: GameX01): Long

    /**
     * Returns all saved game objects
     */
    suspend fun getAllGames(): List<GameX01>
}