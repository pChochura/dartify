package com.pointlessapps.dartify.domain.game.x01.turn.model

sealed interface DoneTurnEvent {
    object AddInput : DoneTurnEvent

    data class AskForNumberOfThrows(val minNumberOfThrows: Int) : DoneTurnEvent

    data class AskForNumberOfDoubles(
        val maxNumberOfDoubles: Int,
    ) : DoneTurnEvent

    data class AskForNumberOfThrowsAndDoubles(
        val minNumberOfThrows: Int,
        val maxNumberOfDoubles: Map<Int, Int>,
    ) : DoneTurnEvent
}
