package com.pointlessapps.dartify.domain.mappers

import com.pointlessapps.dartify.datasource.database.game.model.MatchResolutionStrategy
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy as RemoteMatchResolutionStrategy

internal fun RemoteMatchResolutionStrategy.fromMatchResolutionStrategy() = when (this) {
    RemoteMatchResolutionStrategy.FirstTo -> MatchResolutionStrategy.FirstTo
    RemoteMatchResolutionStrategy.BestOf -> MatchResolutionStrategy.BestOf
}

internal fun MatchResolutionStrategy.toMatchResolutionStrategy() = when (this) {
    MatchResolutionStrategy.FirstTo -> RemoteMatchResolutionStrategy.FirstTo
    MatchResolutionStrategy.BestOf -> RemoteMatchResolutionStrategy.BestOf
}
