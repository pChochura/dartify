package com.pointlessapps.dartify.local.datasource.vibration.di

import com.pointlessapps.dartify.datasource.vibration.VibrationDataSource
import com.pointlessapps.dartify.local.datasource.vibration.LocalVibrationDataSource
import org.koin.dsl.module

internal val vibrationModule = module {
    single<VibrationDataSource> {
        LocalVibrationDataSource(
            rumble = get(),
        )
    }
}
