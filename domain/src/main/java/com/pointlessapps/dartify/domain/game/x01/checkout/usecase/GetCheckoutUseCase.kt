package com.pointlessapps.dartify.domain.game.x01.checkout.usecase

import com.pointlessapps.dartify.domain.game.x01.checkout.CheckoutRepository

class GetCheckoutUseCase(
    private val checkoutRepository: CheckoutRepository,
) {

    operator fun invoke(turnScoreLeft: Int, scoreLeft: Int, numberOfThrows: Int) =
        checkoutRepository.getCheckoutFor(turnScoreLeft, scoreLeft, numberOfThrows)
}
