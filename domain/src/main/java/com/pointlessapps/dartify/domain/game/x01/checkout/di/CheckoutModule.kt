package com.pointlessapps.dartify.domain.game.x01.checkout.di

import com.pointlessapps.dartify.domain.game.x01.checkout.CheckoutRepository
import com.pointlessapps.dartify.domain.game.x01.checkout.CheckoutRepositoryImpl
import com.pointlessapps.dartify.domain.game.x01.checkout.usecase.GetCheckoutUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val checkoutModule = module {
    factoryOf(::GetCheckoutUseCase)

    singleOf(::CheckoutRepositoryImpl).bind(CheckoutRepository::class)
}
