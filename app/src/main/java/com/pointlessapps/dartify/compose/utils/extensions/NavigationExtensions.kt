package com.pointlessapps.dartify.compose.utils.extensions

import com.pointlessapps.dartify.compose.ui.theme.Route
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.popUpTo

internal fun <T> NavController<T>.previousDestination(): T? = backstack.entries.let {
    it.getOrNull(it.lastIndex - 1)?.destination
}

internal fun NavController<Route>.navigateOrPopTo(route: Route?) {
    if (route == null) {
        pop()
    } else if (!popUpTo { it == route }) {
        navigate(route)
    }
}
