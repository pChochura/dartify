package com.pointlessapps.dartify.compose.game.mappers

import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy
import com.pointlessapps.dartify.compose.game.model.MatchResolutionStrategy as ViewMatchResolutionStrategy

internal fun ViewMatchResolutionStrategy.toMatchResolutionStrategy() = when (this) {
    ViewMatchResolutionStrategy.FirstTo -> MatchResolutionStrategy.FirstTo
    ViewMatchResolutionStrategy.BestOf -> MatchResolutionStrategy.BestOf
}

internal fun MatchResolutionStrategy.fromMatchResolutionStrategy() = when (this) {
    MatchResolutionStrategy.FirstTo -> ViewMatchResolutionStrategy.FirstTo
    MatchResolutionStrategy.BestOf -> ViewMatchResolutionStrategy.BestOf
}
