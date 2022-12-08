package com.pointlessapps.dartify.domain.database.players

import com.pointlessapps.dartify.datasource.database.game.ActiveGameDataSource
import com.pointlessapps.dartify.datasource.database.game.x01.GameX01DataSource
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
     * Removes a player from the database with the given [player.id].
     * If the player is associated with active games they'll be deleted.
     */
    fun deletePlayer(player: Player): Flow<Unit>
}

internal class PlayersRepositoryImpl(
    private val playersDataSource: PlayersDataSource,
    private val activeGameDataSource: ActiveGameDataSource,
    private val gameX01DataSource: GameX01DataSource,
) : PlayersRepository {

    override fun getAllPlayers() = playersDataSource.getPlayers().map { players ->
        players.map { it.toPlayer() }
    }

    override fun savePlayer(player: Player) = flow {
        emit(playersDataSource.insertPlayer(player.fromPlayer()))
    }

    override fun deletePlayer(player: Player) = flow {
        val games = gameX01DataSource.getGamesForPlayer(player.id)
        val gamesToDelete = games
            .filter { game -> game.players.all { it.id == player.id } }
            .map { requireNotNull(it.gameId) }
            .toLongArray()
        if (gamesToDelete.isNotEmpty()) {
            gameX01DataSource.deleteGames(*gamesToDelete)
            activeGameDataSource.deleteActiveGames(*gamesToDelete)
        }
        gameX01DataSource.deletePlayer(player.id)
        playersDataSource.deletePlayer(player.fromPlayer())
        emit(Unit)
    }
}
