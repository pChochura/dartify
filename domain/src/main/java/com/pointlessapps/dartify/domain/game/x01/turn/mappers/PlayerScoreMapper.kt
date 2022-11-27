package com.pointlessapps.dartify.domain.game.x01.turn.mappers

import com.pointlessapps.dartify.datasource.game.x01.turn.model.PlayerScore
import com.pointlessapps.dartify.domain.model.Player
import com.pointlessapps.dartify.domain.game.x01.model.PlayerScore as RemotePlayerScore

internal fun PlayerScore.toPlayerScore(players: Map<Long, Player>) = RemotePlayerScore(
    numberOfWonSets = numberOfWonSets,
    numberOfWonLegs = numberOfWonLegs,
    doublePercentage = doublePercentage,
    maxScore = maxScore,
    averageScore = averageScore,
    numberOfDarts = numberOfDarts,
    scoreLeft = scoreLeft,
    lastScore = lastScore,
    player = requireNotNull(players[playerId]),
)
