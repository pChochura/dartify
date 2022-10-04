package com.pointlessapps.dartify.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01Screen
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01Screen
import com.pointlessapps.dartify.compose.home.ui.HomeScreen
import com.pointlessapps.dartify.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavHost(
    navController: NavController<Route> = rememberNavController(
        startDestination = Route.Home,
    ),
) {
    NavBackHandler(controller = navController)
    AnimatedNavHost(controller = navController) {
        when (it) {
            Route.Home -> HomeScreen(
                onNavigate = navController::navigate,
            )
            Route.Players -> {}
            Route.GameSetup.X01 -> GameSetupX01Screen(
                onNavigate = navController::navigate,
            )
            is Route.GameActive.X01 -> GameActiveX01Screen(
                gameSettings = it.gameSettings,
                onNavigate = navController::navigate,
            )
        }
    }
}
