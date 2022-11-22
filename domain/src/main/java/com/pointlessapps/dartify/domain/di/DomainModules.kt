package com.pointlessapps.dartify.domain.di

import com.pointlessapps.dartify.domain.database.di.databaseModules
import com.pointlessapps.dartify.domain.game.x01.di.gameX01Modules
import com.pointlessapps.dartify.domain.vibration.di.vibrationModule

val domainModules = gameX01Modules + vibrationModule + databaseModules
