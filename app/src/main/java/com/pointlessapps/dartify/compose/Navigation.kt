package com.pointlessapps.dartify.compose

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01Screen
import com.pointlessapps.dartify.compose.game.setup.players.ui.SelectPlayersScreen
import com.pointlessapps.dartify.compose.game.setup.x01.ui.GameSetupX01Screen
import com.pointlessapps.dartify.compose.home.ui.HomeScreen
import com.pointlessapps.dartify.compose.ui.theme.Route
import com.pointlessapps.dartify.compose.utils.extensions.navigateOrPopTo
import com.pointlessapps.dartify.compose.utils.extensions.previousDestination
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
                onNavigate = { route -> navController.navigateOrPopTo(route) },
            )
            is Route.Players -> SelectPlayersScreen(
                selectedPlayers = destination.selectedPlayers,
                onPlayersSelected = { selectedPlayers ->
                    val previousDestination = navController.previousDestination()
                    if (previousDestination is Route.Players.PlayersListCallback) {
                        previousDestination.players.value = selectedPlayers
                    }
                    navController.pop()
                },
                onDismissed = { selectedPlayers ->
                    val previousDestination = navController.previousDestination()
                    if (previousDestination is Route.Players.PlayersListCallback) {
                        previousDestination.players.value = selectedPlayers
                    }
                    navController.pop()
                },
            )
            is Route.GameSetup.X01 -> {
                val selectedPlayers by destination.players
                GameSetupX01Screen(
                    selectedPlayers = selectedPlayers,
                    onNavigate = { route -> navController.navigateOrPopTo(route) },
                )
            }
            is Route.GameActive.X01 -> GameActiveX01Screen(
                gameSettings = destination.gameSettings,
                onNavigate = { route -> navController.navigateOrPopTo(route) },
            )
        }
    }
}
