package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.compose.game.active.x01.model.ThrowScore
import com.pointlessapps.dartify.domain.game.x01.checkout.model.Score

internal fun Score.toThrowScore() = ThrowScore(
    value = value,
    multiplier = when (multiplier) {
        Score.Multiplier.Single -> ThrowScore.Multiplier.Single
        Score.Multiplier.Double -> ThrowScore.Multiplier.Double
        Score.Multiplier.Triple -> ThrowScore.Multiplier.Triple
    },
)
