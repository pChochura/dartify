package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.model.PlayerScore
import com.pointlessapps.dartify.compose.game.active.x01.model.PlayerScore as ViewPlayerScore
import com.pointlessapps.dartify.compose.game.model.Player as ViewPlayer

internal fun ViewPlayer.toPlayer() = Player(
    id = id,
    name = name,
    outMode = outMode.toOutMode(),
)

internal fun Player.fromPlayer() = ViewPlayer(
    id = id,
    name = name,
    outMode = outMode.fromGameMode(),
)

internal fun PlayerScore.fromPlayerScore() = ViewPlayerScore(
    numberOfWonSets = numberOfWonSets,
    numberOfWonLegs = numberOfWonLegs,
    doublePercentage = doublePercentage,
    maxScore = maxScore,
    averageScore = averageScore,
    numberOfDarts = numberOfDarts,
    scoreLeft = scoreLeft,
    lastScore = lastScore,
    player = player.fromPlayer(),
)
