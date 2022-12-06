package com.pointlessapps.dartify.domain.game.x01.checkout.model

data class Score(
    val value: Int,
    val multiplier: Multiplier,
) {
    enum class Multiplier { Single, Double, Triple }
}
