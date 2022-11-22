package com.pointlessapps.dartify.domain.game.x01.turn.model

import com.pointlessapps.dartify.domain.game.x01.model.InputScore
import com.pointlessapps.dartify.domain.model.Player
import com.pointlessapps.dartify.domain.game.x01.model.PlayerScore
import com.pointlessapps.dartify.domain.model.GameMode

data class GameState(
    val inMode: GameMode,
    val startingScore: Int,
    val player: Player,
    val playerScores: List<PlayerScore>,
)

sealed interface State

data class CurrentState(
    val score: InputScore?,
    val leg: Int,
    val set: Int,
    val player: Player,
    val playerScores: List<PlayerScore>,
) : State

data class WinState(
    val playerScore: PlayerScore,
) : State
