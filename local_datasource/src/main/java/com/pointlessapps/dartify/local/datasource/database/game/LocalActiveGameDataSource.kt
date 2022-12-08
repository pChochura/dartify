package com.pointlessapps.dartify.local.datasource.database.game

import com.pointlessapps.dartify.datasource.database.game.ActiveGameDataSource
import com.pointlessapps.dartify.datasource.database.game.model.ActiveGame
import com.pointlessapps.dartify.local.datasource.database.game.daos.GameDao
import com.pointlessapps.dartify.local.datasource.database.game.mappers.toActiveGame
import com.pointlessapps.dartify.local.datasource.database.game.mappers.toActiveGameEntity

internal class LocalActiveGameDataSource(
    private val gameDao: GameDao,
) : ActiveGameDataSource {

    override suspend fun insertActiveGame(game: ActiveGame) =
        gameDao.insert(game.toActiveGameEntity())

    override suspend fun getAllActiveGames() =
        gameDao.get().map { it.toActiveGame() }

    override suspend fun deleteActiveGames(vararg gameIds: Long) =
        gameDao.delete(*gameIds)
}
