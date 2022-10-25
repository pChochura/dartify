package com.pointlessapps.dartify.domain.game.x01.turn.usecase

import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.turn.TurnRepository
import com.pointlessapps.dartify.domain.game.x01.model.InputScore

class AddInputUseCase(
    private val turnRepository: TurnRepository,
) {

    operator fun invoke(
        score: InputScore,
        numberOfThrowsOnDouble: Int = 0,
    ) {
        turnRepository.addInput(
            score,
            if (score is InputScore.Dart) score.scores.size else DEFAULT_NUMBER_OF_THROWS,
            numberOfThrowsOnDouble,
        )
    }
}
