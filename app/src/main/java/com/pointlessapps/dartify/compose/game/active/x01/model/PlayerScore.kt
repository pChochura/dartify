package com.pointlessapps.dartify.compose.game.active.x01.model

import android.os.Parcelable
import com.pointlessapps.dartify.compose.game.model.Player
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class PlayerScore(
    val numberOfWonSets: Int,
    val numberOfWonLegs: Int,
    val doublePercentage: Float,
    val maxScore: Int,
    val averageScore: Float,
    val numberOfDarts: Int,
    val scoreLeft: Int,
    val lastScore: Int?,
    val player: Player,
) : Parcelable
