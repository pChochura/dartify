package com.pointlessapps.dartify.rumble.di

import com.pointlessapps.dartify.rumble.Rumble
import com.pointlessapps.dartify.rumble.RumbleImpl
import org.koin.dsl.module

val rumbleModule = module {
    single<Rumble> {
        RumbleImpl(context = get())
    }
}
