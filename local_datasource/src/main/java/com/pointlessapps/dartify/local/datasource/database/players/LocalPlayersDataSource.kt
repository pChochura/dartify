package com.pointlessapps.dartify.local.datasource.database.players

import com.pointlessapps.dartify.datasource.database.players.PlayersDataSource
import com.pointlessapps.dartify.datasource.database.players.model.Player
import com.pointlessapps.dartify.local.datasource.database.players.daos.PlayersDao
import com.pointlessapps.dartify.local.datasource.database.players.entity.PlayerEntity
import com.pointlessapps.dartify.local.datasource.database.players.mappers.toPlayer
import com.pointlessapps.dartify.local.datasource.database.players.mappers.toPlayerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LocalPlayersDataSource(
    private val playersDao: PlayersDao,
) : PlayersDataSource {

    override suspend fun insertPlayer(player: Player) =
        playersDao.insert(player.toPlayerEntity())

    override suspend fun deletePlayer(player: Player) =
        playersDao.delete(player.toPlayerEntity().id)

    override fun getPlayers(): Flow<List<Player>> =
        playersDao.get().map { it.map(PlayerEntity::toPlayer) }
}
