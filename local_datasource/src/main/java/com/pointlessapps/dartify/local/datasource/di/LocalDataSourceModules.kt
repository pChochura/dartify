package com.pointlessapps.dartify.local.datasource.di

import com.pointlessapps.dartify.local.datasource.database.di.databaseModules
import com.pointlessapps.dartify.local.datasource.game.x01.di.gameX01Modules
import com.pointlessapps.dartify.local.datasource.vibration.di.vibrationModule
import com.pointlessapps.dartify.rumble.di.rumbleModule

val localDataSourceModules = gameX01Modules + vibrationModule + rumbleModule + databaseModules
