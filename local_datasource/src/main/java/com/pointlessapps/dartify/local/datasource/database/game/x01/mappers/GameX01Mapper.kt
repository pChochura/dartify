package com.pointlessapps.dartify.local.datasource.database.game.x01.mappers

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.turn.model.InputScore
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.*
import com.pointlessapps.dartify.local.datasource.database.players.mappers.toPlayer

internal fun GameX01.toGameX01Entity() = GameX01Entity(
    currentPlayerId = currentPlayer.id,
    startingScore = startingScore,
    numberOfSets = numberOfSets,
    numberOfLegs = numberOfLegs,
    inMode = inMode,
    matchResolutionStrategy = matchResolutionStrategy,
)

internal fun GameX01WithPlayersEntity.toGameX01(gamePlayersByPlayerIds: Map<Long, GameX01PlayersEntity>): GameX01 {

    fun outMode(playerId: Long) = requireNotNull(gamePlayersByPlayerIds[playerId]?.outMode)
    fun order(playerId: Long) = requireNotNull(gamePlayersByPlayerIds[playerId]?.order)

    return GameX01(
        currentPlayer = currentPlayer.toPlayer(outMode(currentPlayer.id)),
        players = players.sortedBy { order(it.id) }.map { it.toPlayer(outMode(it.id)) },
        inputs = inputs.map { it.toGameX01Input() },
        startingScore = game.startingScore,
        numberOfSets = game.numberOfSets,
        numberOfLegs = game.numberOfLegs,
        inMode = game.inMode,
        matchResolutionStrategy = game.matchResolutionStrategy,
    )
}

private fun GameX01InputWithPlayerEntity.toGameX01Input() = GameX01Input(
    playerId = player.id,
    score = InputScore.Turn(input.score),
    numberOfThrows = input.numberOfThrows,
    numberOfThrowsOnDouble = input.numberOfThrowsOnDouble,
    won = input.won,
    legIndex = input.legIndex,
    setIndex = input.setIndex,
    order = input.order,
)

internal fun GameX01Input.toGameX01InputEntity(gameId: Long) = GameX01InputEntity(
    gameId = gameId,
    playerId = playerId,
    score = score.score(),
    numberOfThrows = numberOfThrows,
    numberOfThrowsOnDouble = numberOfThrowsOnDouble,
    won = won,
    legIndex = legIndex,
    setIndex = setIndex,
    order = order,
)
