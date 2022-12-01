package com.pointlessapps.dartify.domain.database.game.x01

import com.pointlessapps.dartify.datasource.database.game.GameDataSource
import com.pointlessapps.dartify.datasource.database.game.x01.GameX01DataSource
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.domain.database.game.mappers.toActiveGame
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.mappers.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface GameX01Repository {
    /**
     * Saves currently running x01 game into the database. Returns the id of the saved game object.
     * If [isFinished] is equal to false then the game will be saved as an active one
     */
    fun saveCurrentGame(isFinished: Boolean): Flow<Long>

    /**
     * Retrieves a game with the given [gameId] from the database and saves its state as the
     * currently ongoing game
     */
    fun loadGame(gameId: Long): Flow<CurrentState>
}

internal class GameX01RepositoryImpl(
    private val gameDataSource: GameDataSource,
    private val gameX01DataSource: GameX01DataSource,
    private val turnRepository: TurnRepositoryImpl,
) : GameX01Repository {

    override fun saveCurrentGame(isFinished: Boolean) = flow {
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
            matchResolutionStrategy = gameState.matchResolutionStrategy.fromMatchResolutionStrategy(),
        )
        val gameId = gameX01DataSource.insertGame(game)
        if (!isFinished) {
            gameDataSource.insertActiveGame(game.toActiveGame(gameId))
        }
        emit(gameId)
    }

    override fun loadGame(gameId: Long) = flow {
        val game = gameX01DataSource.getGame(gameId)
        gameX01DataSource.deleteGame(gameId)
        emit(
            turnRepository.load(
                players = game.players.map { it.toPlayer() },
                currentPlayer = game.currentPlayer.toPlayer(),
                startingScore = game.startingScore,
                inMode = game.inMode.toGameMode(),
                numberOfSets = game.numberOfSets,
                numberOfLegs = game.numberOfLegs,
                matchResolutionStrategy = game.matchResolutionStrategy.toMatchResolutionStrategy(),
                inputsHistory = game.inputs,
            ),
        )
    }
}
