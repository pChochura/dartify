package com.pointlessapps.dartify.local.datasource.game.x01.checkout.di

import com.pointlessapps.dartify.datasource.game.x01.checkout.CheckoutDataSource
import com.pointlessapps.dartify.local.datasource.game.x01.checkout.LocalCheckoutDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val checkoutModule = module {
    singleOf(::LocalCheckoutDataSource).bind(CheckoutDataSource::class)
}
