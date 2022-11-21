package com.pointlessapps.dartify.domain.vibration.di

import com.pointlessapps.dartify.domain.vibration.VibrationRepository
import com.pointlessapps.dartify.domain.vibration.VibrationRepositoryImpl
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val vibrationModule = module {
    factoryOf(::VibrateUseCase)

    singleOf(::VibrationRepositoryImpl).bind(VibrationRepository::class)
}
