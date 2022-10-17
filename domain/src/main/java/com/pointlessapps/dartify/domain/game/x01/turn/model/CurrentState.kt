package com.pointlessapps.dartify.domain.game.x01.turn.model

import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.model.PlayerScore

data class CurrentState(
    val score: Int? = null,
    val leg: Int? = null,
    val set: Int? = null,
    val player: Player? = null,
    val playerScores: List<PlayerScore>? = null,
    val won: Boolean? = null,
)
