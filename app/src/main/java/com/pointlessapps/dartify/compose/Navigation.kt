package com.pointlessapps.dartify.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01Screen
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01Screen
import com.pointlessapps.dartify.compose.game.setup.players.ui.SelectPlayersScreen
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
    AnimatedNavHost(controller = navController) { destination ->
        when (destination) {
            Route.Home -> HomeScreen(
                onNavigate = { route -> navigate(navController, route) },
            )
            is Route.Players -> SelectPlayersScreen(
                selectedPlayers = destination.selectedPlayers,
                onPlayersSelected = {
                    navController.pop()
                    destination.callback(it)
                },
            )
            Route.GameSetup.X01 -> GameSetupX01Screen(
                onNavigate = { route -> navigate(navController, route) },
            )
            is Route.GameActive.X01 -> GameActiveX01Screen(
                gameSettings = destination.gameSettings,
                onNavigate = { route -> navigate(navController, route) },
            )
        }
    }
}

private fun navigate(navController: NavController<Route>, route: Route?) {
    if (route == null) {
        navController.pop()
    } else if (!navController.popUpTo { it == route }) {
        navController.navigate(route)
    }
}
