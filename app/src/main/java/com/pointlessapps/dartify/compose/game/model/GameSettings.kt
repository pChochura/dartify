package com.pointlessapps.dartify.compose.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

internal sealed interface GameSettings : Parcelable {
    @Parcelize
    data class NewGame(
        val startingScore: Int,
        val numberOfSets: Int,
        val numberOfLegs: Int,
        val matchResolutionStrategy: MatchResolutionStrategy,
        val players: List<Player>,
        val inMode: GameMode,
    ) : GameSettings

    @Parcelize
    data class LoadGame(val id: Long, val type: GameType) : GameSettings
}
