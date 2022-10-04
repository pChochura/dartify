package com.pointlessapps.dartify.compose.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class GameSettings(
    val startingScore: Int,
    val numberOfSets: Int,
    val numberOfLegs: Int,
    val resolutionPredicate: (sets: Int, legs: Int) -> Boolean,
    val players: List<Player>,
) : Parcelable
