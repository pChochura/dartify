package com.pointlessapps.dartify.domain.database.game.x01

import com.pointlessapps.dartify.datasource.database.game.x01.GameX01DataSource
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepositoryImpl
import com.pointlessapps.dartify.domain.mappers.fromGameMode
import com.pointlessapps.dartify.domain.mappers.fromPlayer
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GameX01Repository {
    /**
     * Saves currently running x01 game into the database. Returns the id of the saved game object
     */
    fun saveCurrentGame(): Flow<Long>

    /**
     * Returns all saved x01 game objects stored in the database
     */
    fun getAllGames(): Flow<List<GameX01>>
}

internal class GameX01RepositoryImpl(
    private val gameX01DataSource: GameX01DataSource,
    private val turnRepository: TurnRepositoryImpl,
) : GameX01Repository {

    override fun getAllGames() = flow {
        emit(gameX01DataSource.getAllGames())
    }

    override fun saveCurrentGame() = flow {
        val inputsHistory = turnRepository.getAllInputsHistory()
        val gameState = turnRepository.getGameState()
        val game = GameX01(
            currentPlayer = gameState.player.fromPlayer(),
            players = gameState.playerScores.map { it.player.fromPlayer() },
            inputs = inputsHistory,
            startingScore = gameState.startingScore,
            numberOfSets = gameState.numberOfSets,
            numberOfLegs = gameState.numberOfLegs,
            inMode = gameState.inMode.fromGameMode(),
        )
        emit(gameX01DataSource.insertGame(game))
    }
}
