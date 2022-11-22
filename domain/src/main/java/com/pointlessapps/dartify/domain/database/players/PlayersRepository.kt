package com.pointlessapps.dartify.domain.database.players

import com.pointlessapps.dartify.datasource.database.players.PlayersDataSource
import com.pointlessapps.dartify.domain.mappers.fromPlayer
import com.pointlessapps.dartify.domain.mappers.toPlayer
import com.pointlessapps.dartify.domain.model.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

interface PlayersRepository {
    /**
     * Returns all players stored in the database continuously
     */
    fun getAllPlayers(): Flow<List<Player>>

    /**
     * Adds a player to the database
     */
    fun savePlayer(player: Player): Flow<Unit>

    /**
     * Removes a player from the database with the given [player.id]
     */
    fun deletePlayer(player: Player): Flow<Unit>
}

internal class PlayersRepositoryImpl(
    private val playersDataSource: PlayersDataSource,
) : PlayersRepository {

    override fun getAllPlayers() = playersDataSource.getPlayers().map { players ->
        players.map { it.toPlayer() }
    }

    override fun savePlayer(player: Player) = flow {
        emit(playersDataSource.insertPlayer(player.fromPlayer()))
    }

    override fun deletePlayer(player: Player) = flow {
        emit(playersDataSource.deletePlayer(player.fromPlayer()))
    }
}
