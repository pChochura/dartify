package com.pointlessapps.dartify.compose.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.pointlessapps.dartify.LocalSnackbarHostState
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.home.model.ActiveGame
import com.pointlessapps.dartify.compose.ui.components.*
import com.pointlessapps.dartify.compose.ui.theme.Route
import kotlinx.coroutines.launch
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
                .padding(vertical = dimensionResource(id = R.dimen.margin_huge))
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
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big))
            .weight(1f),
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
        modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big)),
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
private fun FavouriteGames(
    favouriteGames: List<ActiveGame>,
    onPlayClicked: (ActiveGame) -> Unit,
) {
    AnimatedContent(targetState = favouriteGames) { games ->
        if (games.isEmpty()) {
            return@AnimatedContent
        }

        BoxWithConstraints {
            val coroutineScope = rememberCoroutineScope()
            val state = rememberLazyListState()

            LazyRow(
                state = state,
                flingBehavior = rememberSnapFlingBehavior(state),
                contentPadding = PaddingValues(
                    horizontal = maxWidth * 0.2f,
                ),
            ) {
                items(games) {
                    Box(
                        modifier = Modifier.fillParentMaxWidth(),
                        contentAlignment = Alignment.Center,
                        content = { FavouriteGameButton(it, onPlayClicked) },
                    )
                }
            }

            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.spacedBy(maxWidth * 0.5f),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val firstIndex by remember { derivedStateOf { state.firstVisibleItemIndex } }
                val leftButtonAlpha by animateFloatAsState(
                    if (!state.isScrollInProgress && firstIndex > 0) {
                        1f
                    } else {
                        0.3f
                    },
                )
                val rightButtonAlpha by animateFloatAsState(
                    if (!state.isScrollInProgress && firstIndex < games.lastIndex) {
                        1f
                    } else {
                        0.3f
                    },
                )

                IconButton(
                    modifier = Modifier.alpha(leftButtonAlpha),
                    enabled = leftButtonAlpha == 1f,
                    onClick = {
                        coroutineScope.launch { state.animateScrollToItem(state.firstVisibleItemIndex - 1) }
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                        painter = painterResource(id = R.drawable.ic_arrow_left),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.previous_fav_game),
                    )
                }
                IconButton(
                    modifier = Modifier.alpha(rightButtonAlpha),
                    enabled = rightButtonAlpha == 1f,
                    onClick = {
                        coroutineScope.launch { state.animateScrollToItem(state.firstVisibleItemIndex + 1) }
                    },
                ) {
                    Icon(
                        modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                        painter = painterResource(id = R.drawable.ic_arrow_right),
                        tint = MaterialTheme.colors.onBackground,
                        contentDescription = stringResource(id = R.string.previous_fav_game),
                    )
                }
            }
        }
    }
}

@Composable
private fun FavouriteGameButton(
    activeGame: ActiveGame,
    onPlayClicked: (ActiveGame) -> Unit,
) {
    ComposeButton(
        label = stringResource(id = R.string.saved_game),
        onClick = { onPlayClicked(activeGame) },
        buttonModel = defaultComposeButtonModel().copy(
            size = ComposeButtonSize.Big,
            shape = ComposeButtonShape.Pill,
            backgroundColor = colorResource(id = R.color.red),
            content = ComposeButtonContent.Custom {
                Column {
                    ComposeText(
                        text = activeGame.title,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = defaultComposeTextStyle().copy(
                            typography = MaterialTheme.typography.h1.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                            ),
                            textAlign = TextAlign.Center,
                        ),
                    )
                    ComposeText(
                        text = activeGame.subtitle,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = defaultComposeTextStyle().copy(
                            typography = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center,
                        ),
                    )
                }
            },
        ),
    )
}

@Composable
private fun BottomButtons(
    onStatsClicked: () -> Unit,
    onDailyChallengeClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = dimensionResource(id = R.dimen.margin_semi_big))
            .fillMaxWidth(),
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
