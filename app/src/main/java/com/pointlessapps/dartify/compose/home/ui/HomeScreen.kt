package com.pointlessapps.dartify.compose.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.pointlessapps.dartify.LocalSnackbarHostState
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.home.model.ActiveGame
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.compose.ui.theme.Route
import org.koin.androidx.compose.getViewModel

@Composable
internal fun HomeScreen(
    viewModel: HomeViewModel = getViewModel(),
    onNavigate: (Route?) -> Unit,
) {
    val localSnackbarHostState = LocalSnackbarHostState.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshFavouriteGames()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when (it) {
                is HomeEvent.Navigate -> onNavigate(it.route)
                is HomeEvent.ShowSnackbar -> localSnackbarHostState.showSnackbar(it.message)
            }
        }
    }

    ComposeLoader(enabled = viewModel.state.isLoading)

    ComposeScaffoldLayout { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                    vertical = dimensionResource(id = R.dimen.margin_huge),
                )
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_huge),
                Alignment.Bottom,
            ),
        ) {
            LogoText()
            MainButtons(
                onTrainingClicked = viewModel::onTrainingClicked,
                onPlayClicked = viewModel::onPlayClicked,
            )
            FavouriteGames(
                favouriteGames = viewModel.state.favouriteGames,
                onPlayClicked = viewModel::onPlayClicked,
            )
            BottomButtons(
                onStatsClicked = viewModel::onStatsClicked,
                onDailyChallengeClicked = viewModel::onDailyChallengeClicked,
                onSettingsClicked = viewModel::onSettingsClicked,
            )
        }
    }
}

@Composable
private fun ColumnScope.LogoText() {
    Box(
        modifier = Modifier.weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        ComposeText(
            text = stringResource(id = R.string.app_name),
            textStyle = defaultComposeTextStyle().let { style ->
                style.copy(
                    textColor = colorResource(id = R.color.red),
                    textAlign = TextAlign.Center,
                    typography = style.typography.copy(
                        fontSize = 64.sp,
                    ),
                )
            },
        )
    }
}

@Composable
private fun MainButtons(
    onTrainingClicked: () -> Unit,
    onPlayClicked: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_huge),
        ),
    ) {
        ComposeButton(
            label = stringResource(id = R.string.training),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_improve,
                size = ComposeButtonSize.Big,
                shape = ComposeButtonShape.Circle,
            ),
            onClick = onTrainingClicked,
        )
        ComposeButton(
            label = stringResource(id = R.string.play),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_darts,
                size = ComposeButtonSize.Big,
                shape = ComposeButtonShape.Circle,
            ),
            onClick = onPlayClicked,
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun FavouriteGames(
    favouriteGames: List<ActiveGame>,
    onPlayClicked: (ActiveGame) -> Unit,
) {
    HorizontalPager(count = favouriteGames.size) {
        ComposeButton(
            label = favouriteGames[it].title,
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_play,
                size = ComposeButtonSize.Big,
                shape = ComposeButtonShape.Pill,
                backgroundColor = colorResource(id = R.color.red),
            ),
            onClick = { onPlayClicked(favouriteGames[currentPage]) },
        )
    }
}

@Composable
private fun BottomButtons(
    onStatsClicked: () -> Unit,
    onDailyChallengeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ComposeButton(
            label = stringResource(id = R.string.stats),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_stats,
                size = ComposeButtonSize.Medium,
                shape = ComposeButtonShape.Circle,
            ),
            onClick = onStatsClicked,
        )
        ComposeButton(
            label = stringResource(id = R.string.daily_challenge),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_calendar,
                size = ComposeButtonSize.Medium,
                shape = ComposeButtonShape.Pill,
                backgroundColor = colorResource(id = R.color.red),
            ),
            onClick = onDailyChallengeClicked,
        )
        ComposeButton(
            label = stringResource(id = R.string.settings),
            buttonModel = defaultComposeButtonModel().copy(
                icon = R.drawable.ic_settings,
                size = ComposeButtonSize.Medium,
                shape = ComposeButtonShape.Circle,
            ),
            onClick = onSettingsClicked,
        )
    }
}
