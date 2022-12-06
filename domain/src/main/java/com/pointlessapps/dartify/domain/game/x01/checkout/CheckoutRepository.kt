package com.pointlessapps.dartify.domain.game.x01.checkout

import com.pointlessapps.dartify.datasource.game.x01.checkout.CheckoutDataSource
import com.pointlessapps.dartify.domain.game.x01.checkout.mapper.toScore
import com.pointlessapps.dartify.domain.game.x01.checkout.model.Score

interface CheckoutRepository {
    /**
     * Returns a list of values that will result in a checkout for the given [scoreLeft]
     * and [numberOfThrows] or if it's not possible tries to find a checkout
     * for the [turnScoreLeft] with no constraints on the number of throws and then truncates
     * the list to match the size to the [numberOfThrows]
     */
    fun getCheckoutFor(turnScoreLeft: Int, scoreLeft: Int, numberOfThrows: Int): List<Score>?
}

internal class CheckoutRepositoryImpl(
    private val checkoutDataSource: CheckoutDataSource,
) : CheckoutRepository {

    override fun getCheckoutFor(turnScoreLeft: Int, scoreLeft: Int, numberOfThrows: Int) =
        checkoutDataSource.getCheckoutFor(turnScoreLeft, scoreLeft, numberOfThrows)
            ?.map { it.toScore() }
}
