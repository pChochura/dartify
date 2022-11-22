package com.pointlessapps.dartify.compose.game.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import java.util.*

@Stable
@Parcelize
internal data class Player(
    val id: Long = UUID.randomUUID().mostSignificantBits,
    val name: String,
    val outMode: GameMode? = null,
    val botOptions: BotOptions? = null,
) : Parcelable

@Parcelize
internal data class BotOptions(
    val average: Float,
) : Parcelable
