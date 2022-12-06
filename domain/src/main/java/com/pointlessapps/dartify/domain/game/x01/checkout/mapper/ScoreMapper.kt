package com.pointlessapps.dartify.domain.game.x01.checkout.mapper

import com.pointlessapps.dartify.datasource.game.x01.checkout.model.Score
import com.pointlessapps.dartify.domain.game.x01.checkout.model.Score as RemoteScore

internal fun Score.toScore() = RemoteScore(
    value = value,
    multiplier = multiplier.toMultiplier(),
)

private fun Score.Multiplier.toMultiplier() = when (this) {
    Score.Multiplier.Single -> RemoteScore.Multiplier.Single
    Score.Multiplier.Double -> RemoteScore.Multiplier.Double
    Score.Multiplier.Triple -> RemoteScore.Multiplier.Triple
}
