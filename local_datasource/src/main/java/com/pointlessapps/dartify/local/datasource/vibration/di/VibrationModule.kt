package com.pointlessapps.dartify.local.datasource.vibration.di

import com.pointlessapps.dartify.datasource.vibration.VibrationDataSource
import com.pointlessapps.dartify.local.datasource.vibration.LocalVibrationDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val vibrationModule = module {
    singleOf(::LocalVibrationDataSource).bind(VibrationDataSource::class)
}
