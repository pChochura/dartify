package com.pointlessapps.dartify.domain.database.players.usecase

import com.pointlessapps.dartify.domain.database.players.PlayersRepository

class GetAllPlayersUseCase(
    private val playersRepository: PlayersRepository,
) {

    operator fun invoke() = playersRepository.getAllPlayers()
}
