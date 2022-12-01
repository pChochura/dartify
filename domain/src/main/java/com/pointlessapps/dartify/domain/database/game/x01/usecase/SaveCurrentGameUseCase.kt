package com.pointlessapps.dartify.domain.database.game.x01.usecase

import com.pointlessapps.dartify.domain.database.game.x01.GameX01Repository

class SaveCurrentGameUseCase(
    private val gameX01Repository: GameX01Repository,
) {

    operator fun invoke(isGameFinished: Boolean) = gameX01Repository.saveCurrentGame(isGameFinished)
}
