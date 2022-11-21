package com.pointlessapps.dartify.rumble.di

import com.pointlessapps.dartify.rumble.Rumble
import com.pointlessapps.dartify.rumble.RumbleImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val rumbleModule = module {
    singleOf(::RumbleImpl).bind(Rumble::class)
}
