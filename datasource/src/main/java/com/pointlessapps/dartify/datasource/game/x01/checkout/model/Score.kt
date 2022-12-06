package com.pointlessapps.dartify.datasource.game.x01.checkout.model

data class Score(
    val value: Int,
    val multiplier: Multiplier,
) {
    enum class Multiplier(val value: Int) {
        Single(1), Double(2), Triple(3)
    }
}

val Score.score get() = value * multiplier.value

val Int.S get() = Score(this, Score.Multiplier.Single)
val Int.D get() = Score(this, Score.Multiplier.Double)
val Int.T get() = Score(this, Score.Multiplier.Triple)
