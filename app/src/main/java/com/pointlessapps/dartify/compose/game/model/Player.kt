package com.pointlessapps.dartify.compose.game.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
internal open class Player(
    open val id: Long = UUID.randomUUID().mostSignificantBits,
    open val name: String,
    open val outMode: GameMode? = null,
) : Parcelable {

    fun copy(
        id: Long = this.id,
        name: String = this.name,
        outMode: GameMode? = this.outMode,
    ) = Player(
        id = id,
        name = name,
        outMode = outMode,
    )
}

@Parcelize
internal data class Bot(
    val average: Float,
    override val id: Long = UUID.randomUUID().mostSignificantBits,
    override val name: String,
    override val outMode: GameMode? = null,
) : Player(
    id = id,
    name = name,
    outMode = outMode,
)
