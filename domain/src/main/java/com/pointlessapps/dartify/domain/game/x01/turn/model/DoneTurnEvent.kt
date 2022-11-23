package com.pointlessapps.dartify.domain.game.x01.turn.model

import com.pointlessapps.dartify.domain.game.x01.model.InputScore

sealed interface DoneTurnEvent {
    data class AddInput(val inputScore: InputScore) : DoneTurnEvent

    data class AskForNumberOfThrows(val minNumberOfThrows: Int) : DoneTurnEvent

    data class AskForNumberOfDoubles(
        val minNumberOfDoubles: Int,
        val maxNumberOfDoubles: Int,
    ) : DoneTurnEvent

    data class AskForNumberOfThrowsAndDoubles(
        val minNumberOfThrows: Int,
        val maxNumberOfDoubles: Map<Int, Int>,
    ) : DoneTurnEvent
}
