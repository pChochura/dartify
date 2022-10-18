package com.pointlessapps.dartify.domain.game.x01.model

import com.pointlessapps.dartify.domain.game.x01.score.model.GameMode
import java.util.*

open class Player(
    val name: String,
    val outMode: GameMode,
    val id: Long = UUID.randomUUID().mostSignificantBits,
)
