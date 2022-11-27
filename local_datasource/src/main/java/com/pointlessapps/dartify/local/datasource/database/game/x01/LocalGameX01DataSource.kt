package com.pointlessapps.dartify.local.datasource.database.game.x01

import com.pointlessapps.dartify.datasource.database.game.x01.GameX01DataSource
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.local.datasource.database.game.x01.daos.GameX01Dao
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01PlayersEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.mappers.toGameX01
import com.pointlessapps.dartify.local.datasource.database.game.x01.mappers.toGameX01Entity
import com.pointlessapps.dartify.local.datasource.database.game.x01.mappers.toGameX01InputEntity

internal class LocalGameX01DataSource(
    private val gameX01Dao: GameX01Dao,
) : GameX01DataSource {

    override suspend fun insertGame(game: GameX01): Long {
        val gameId = gameX01Dao.insert(game.toGameX01Entity())
        val gameX01Players = game.players.mapIndexed { order, player ->
            GameX01PlayersEntity(
                gameId = gameId,
                playerId = player.id,
                outMode = player.outMode,
                order = order,
            )
        }
        val gameX01Inputs = game.inputs.map {
            it.toGameX01InputEntity(gameId)
        }
        gameX01Dao.insertGamePlayers(gameX01Players)
        gameX01Dao.insertGameInputs(gameX01Inputs)

        return gameId
    }

    override suspend fun getGame(gameId: Long): GameX01 {
        val gamePlayersByPlayerIds = gameX01Dao.getGamePlayers(gameId).associateBy { it.playerId }
        return gameX01Dao.get(gameId).toGameX01(gamePlayersByPlayerIds)
    }

    override suspend fun deleteGame(gameId: Long) =
        gameX01Dao.delete(gameId)
}
