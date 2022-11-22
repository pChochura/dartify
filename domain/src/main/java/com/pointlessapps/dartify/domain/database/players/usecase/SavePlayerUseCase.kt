package com.pointlessapps.dartify.domain.database.players.usecase

import com.pointlessapps.dartify.domain.database.players.PlayersRepository
import com.pointlessapps.dartify.domain.model.Player

class SavePlayerUseCase(
    private val playersRepository: PlayersRepository,
) {

    operator fun invoke(player: Player) = playersRepository.savePlayer(player)
}
