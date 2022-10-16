package com.pointlessapps.dartify.compose.game.active.x01.ui.input.score

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.ui.GameActiveX01ViewModel
import com.pointlessapps.dartify.compose.ui.components.ComposeExactGrid
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.utils.conditional
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun ThreeDartsInputKeyboard(
    onPossibleCheckoutRequested: () -> Int? = { null },
    onQuickScoreClicked: (Int) -> Unit,
    onKeyClicked: (Int) -> Unit,
    onUndoClicked: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    QuickScores(
        onPossibleCheckoutRequested = onPossibleCheckoutRequested,
        onQuickScoreClicked = onQuickScoreClicked,
    )
    Keyboard(
        onKeyClicked = onKeyClicked,
        onUndoClicked = onUndoClicked,
        onDoneClicked = onDoneClicked,
    )
}

@Composable
private fun QuickScores(
    onPossibleCheckoutRequested: () -> Int? = { null },
    onQuickScoreClicked: (Int) -> Unit,
) {
    val possibleCheckout = onPossibleCheckoutRequested()

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        userScrollEnabled = false,
    ) {
        itemsIndexed(GameActiveX01ViewModel.QUICK_SCORES) { index, item ->
            if (
                possibleCheckout != null &&
                index == GameActiveX01ViewModel.QUICK_SCORES.lastIndex
            ) {
                QuickScore(
                    score = possibleCheckout,
                    onQuickScoreClicked = {
                        onQuickScoreClicked(possibleCheckout)
                    },
                    hasAccent = true,
                )
            } else {
                QuickScore(
                    score = item,
                    onQuickScoreClicked = {
                        onQuickScoreClicked(item)
                    },
                )
            }
        }
    }
}

@Composable
private fun QuickScore(score: Int, onQuickScoreClicked: () -> Unit, hasAccent: Boolean = false) {
    Box(
        modifier = Modifier
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .background(
                if (hasAccent) {
                    colorResource(id = R.color.red)
                } else {
                    MaterialTheme.colors.secondary
                },
            )
            .clickable(
                role = Role.Button,
                onClickLabel = score.toString(),
                onClick = onQuickScoreClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeText(
            text = score.toString(),
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.let {
                    it.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = it.fontSize.value.scaledSp(),
                    )
                },
            ),
        )
    }
}

@Composable
private fun Keyboard(
    onKeyClicked: (Int) -> Unit,
    onUndoClicked: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    val rows = 4
    val columns = 3
    ComposeExactGrid(
        rows = rows,
        columns = columns,
        modifier = Modifier.fillMaxSize(),
    ) { x, y ->
        when {
            y < rows - 1 -> Key(
                label = "${y * columns + x + 1}",
                onKeyClicked = { onKeyClicked(y * columns + x + 1) },
            )
            x == 0 -> IconKey(
                icon = R.drawable.ic_undo,
                label = stringResource(id = R.string.undo),
                onKeyClicked = onUndoClicked,
            )
            x == columns / 2 -> Key(
                label = "0",
                onKeyClicked = { onKeyClicked(0) },
            )
            x == columns - 1 -> IconKey(
                icon = R.drawable.ic_done,
                label = stringResource(id = R.string.done),
                onKeyClicked = onDoneClicked,
                hasAccent = true,
            )
        }
    }
}

@Composable
private fun Key(label: String, onKeyClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                onClick = onKeyClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        contentAlignment = Alignment.Center,
    ) {
        ComposeText(
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.scaledSp(),
                ),
            ),
        )
    }
}

@Composable
private fun IconKey(
    @DrawableRes icon: Int,
    label: String,
    onKeyClicked: () -> Unit,
    hasAccent: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .conditional(hasAccent) {
                background(colorResource(id = R.color.red))
            }
            .clickable(
                role = Role.Button,
                onClickLabel = label,
                onClick = onKeyClicked,
            )
            .padding(vertical = dimensionResource(id = R.dimen.margin_small)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_nano),
            Alignment.CenterVertically,
        ),
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.key_button_icon_size)),
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = MaterialTheme.colors.onSecondary,
        )
        ComposeText(
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                textAlign = TextAlign.Center,
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h2.let {
                    it.copy(
                        fontSize = it.fontSize.value.scaledSp(),
                    )
                },
            ),
        )
    }
}
