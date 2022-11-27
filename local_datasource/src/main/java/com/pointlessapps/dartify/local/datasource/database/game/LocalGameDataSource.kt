package com.pointlessapps.dartify.local.datasource.database.game

import com.pointlessapps.dartify.datasource.database.game.GameDataSource
import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame
import com.pointlessapps.dartify.local.datasource.database.game.daos.GameDao
import com.pointlessapps.dartify.local.datasource.database.game.mappers.toActiveGame
import com.pointlessapps.dartify.local.datasource.database.game.mappers.toActiveGameEntity

internal class LocalGameDataSource(
    private val gameDao: GameDao,
) : GameDataSource {

    override suspend fun insertActiveGame(game: ActiveGame) =
        gameDao.insert(game.toActiveGameEntity())

    override suspend fun getAllActiveGames() =
        gameDao.get().map { it.toActiveGame() }

    override suspend fun deleteActiveGame(gameId: Long) =
        gameDao.delete(gameId)
}
