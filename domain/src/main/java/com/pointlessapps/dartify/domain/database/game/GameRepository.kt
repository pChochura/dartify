package com.pointlessapps.dartify.domain.database.game

import com.pointlessapps.dartify.datasource.database.game.GameDataSource
import com.pointlessapps.dartify.domain.database.game.mappers.toActiveGame
import com.pointlessapps.dartify.domain.database.game.model.ActiveGame
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GameRepository {
    /**
     * Returns all active game objects stored in the database
     */
    fun getAllActiveGames(): Flow<List<ActiveGame>>

    /**
     * Removes an active game with the given id from the database
     */
    fun deleteActiveGame(gameId: Long): Flow<Unit>
}

internal class GameRepositoryImpl(
    private val gameDataSource: GameDataSource,
): GameRepository {

    override fun getAllActiveGames() = flow {
        emit(gameDataSource.getAllActiveGames().map { it.toActiveGame() })
    }

    override fun deleteActiveGame(gameId: Long) = flow {
        emit(gameDataSource.deleteActiveGame(gameId))
    }
}
