package com.pointlessapps.dartify.domain.game.x01.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.ScoreRepository
import com.pointlessapps.dartify.domain.game.x01.model.OutMode

class ShouldAsForNumberOfDoublesUseCase(
    private val scoreRepository: ScoreRepository,
) {

    operator fun invoke(
        score: Int,
        scoreLeft: Int,
        numberOfThrows: Int = DEFAULT_NUMBER_OF_THROWS,
        outMode: OutMode,
    ) = scoreRepository.shouldAsForNumberOfDoubles(
        score,
        scoreLeft,
        numberOfThrows,
        outMode,
    )
}
