package com.pointlessapps.dartify.datasource.database.game.x01.model

import com.pointlessapps.dartify.datasource.database.game.model.MatchResolutionStrategy
import com.pointlessapps.dartify.datasource.database.model.GameMode
import com.pointlessapps.dartify.datasource.database.players.model.Player

data class GameX01(
    val gameId: Long?,
    val currentPlayer: Player,
    val players: List<Player>,
    val inputs: List<GameX01Input>,
    val startingScore: Int,
    val numberOfSets: Int,
    val numberOfLegs: Int,
    val inMode: GameMode,
    val matchResolutionStrategy: MatchResolutionStrategy,
)
