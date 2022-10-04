package com.pointlessapps.dartify.di

import com.pointlessapps.dartify.compose.game.active.di.gameActiveModules
import com.pointlessapps.dartify.compose.game.setup.di.gameSetupModules
import com.pointlessapps.dartify.compose.home.di.homeModule

val applicationModules = homeModule + gameSetupModules + gameActiveModules
