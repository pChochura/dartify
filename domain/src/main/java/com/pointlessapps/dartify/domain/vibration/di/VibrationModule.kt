package com.pointlessapps.dartify.domain.vibration.di

import com.pointlessapps.dartify.domain.vibration.VibrationRepository
import com.pointlessapps.dartify.domain.vibration.VibrationRepositoryImpl
import com.pointlessapps.dartify.domain.vibration.usecase.VibrateUseCase
import org.koin.dsl.module

internal val vibrationModule = module {
    single<VibrationRepository> {
        VibrationRepositoryImpl(
            vibrationDataSource = get(),
        )
    }

    factory {
        VibrateUseCase(
            vibrationRepository = get(),
        )
    }
}
