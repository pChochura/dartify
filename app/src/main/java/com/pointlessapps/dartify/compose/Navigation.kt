package com.pointlessapps.dartify.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01Screen
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01Screen
import com.pointlessapps.dartify.compose.home.ui.HomeScreen
import com.pointlessapps.dartify.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.rememberNavController

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavHost(
    navController: NavController<Route> = rememberNavController(
        startDestination = Route.GameActive.X01,
    ),
) {
    NavBackHandler(controller = navController)
    AnimatedNavHost(controller = navController) {
        when (it) {
            Route.Home -> HomeScreen()
            Route.GameSetup.X01 -> GameSetupX01Screen()
            Route.GameActive.X01 -> GameActiveX01Screen()
        }
    }
}
