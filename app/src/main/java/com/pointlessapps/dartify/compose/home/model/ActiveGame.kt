package com.pointlessapps.dartify.compose.home.model

import android.os.Parcelable
import com.pointlessapps.dartify.compose.game.model.GameType
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class ActiveGame(
    val gameId: Long,
    val title: String,
    val subtitle: String,
    val type: GameType,
) : Parcelable
