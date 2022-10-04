package com.pointlessapps.dartify.compose.game.model

import android.os.Parcelable
import com.pointlessapps.dartify.compose.game.setup.x01.ui.model.GameMode
import kotlinx.parcelize.Parcelize

@Parcelize
internal open class Player(
    open val name: String,
    open val outMode: GameMode? = null,
) : Parcelable

@Parcelize
internal data class Bot(
    val average: Float,
    override val name: String,
    override val outMode: GameMode? = null,
) : Player(
    name = name,
    outMode = outMode,
)
