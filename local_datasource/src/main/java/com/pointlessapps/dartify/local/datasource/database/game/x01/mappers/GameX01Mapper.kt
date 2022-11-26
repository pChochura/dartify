package com.pointlessapps.dartify.local.datasource.database.game.x01.mappers

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01
import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01Entity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01InputEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01InputWithPlayerEntity
import com.pointlessapps.dartify.local.datasource.database.game.x01.entity.GameX01WithPlayersEntity
import com.pointlessapps.dartify.local.datasource.database.players.mappers.toPlayer

internal fun GameX01.toGameX01Entity() = GameX01Entity(
    currentPlayerId = currentPlayer.id,
    startingScore = startingScore,
    numberOfSets = numberOfSets,
    numberOfLegs = numberOfLegs,
    inMode = inMode,
)

internal fun GameX01WithPlayersEntity.toGameX01() = GameX01(
    currentPlayer = currentPlayer.toPlayer(),
    players = players.map { it.toPlayer() },
    inputs = inputs.map { it.toGameX01Input() },
    startingScore = game.startingScore,
    numberOfSets = game.numberOfSets,
    numberOfLegs = game.numberOfLegs,
    inMode = game.inMode,
)

private fun GameX01InputWithPlayerEntity.toGameX01Input() = GameX01Input(
    playerId = player.id,
    score = InputScore.Turn(input.score),
    numberOfThrows = input.numberOfThrows,
    numberOfThrowsOnDouble = input.numberOfThrowsOnDouble,
    type = input.type,
    won = input.won,
)

internal fun GameX01Input.toGameX01InputEntity(gameId: Long) = GameX01InputEntity(
    gameId = gameId,
    playerId = playerId,
    score = score.score(),
    numberOfThrows = numberOfThrows,
    numberOfThrowsOnDouble = numberOfThrowsOnDouble,
    type = type,
    won = won,
)
