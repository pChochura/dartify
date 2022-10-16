package com.pointlessapps.dartify.compose.game.active.x01.ui.input.score

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun ThreeDartsInputScore(
    finishSuggestion: String?,
    currentInputScore: Int,
    onClearClicked: () -> Unit,
) {
    val suggestionTextFontSize by animateFloatAsState(if (currentInputScore == 0) 40f else 16f)
    val suggestionTextAlignment by animateFloatAsState(if (currentInputScore == 0) 0f else -1f)
    val currentInputScoreOpacity by animateFloatAsState(
        if (finishSuggestion == null || currentInputScore != 0) {
            1f
        } else {
            0f
        },
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                vertical = dimensionResource(id = R.dimen.margin_small),
            ),
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(BiasAlignment(suggestionTextAlignment, 0f)),
            visible = finishSuggestion != null,
        ) {
            ComposeText(
                text = finishSuggestion.toString(),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.h1.copy(
                        fontSize = suggestionTextFontSize.scaledSp(),
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
        }
        ComposeText(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(currentInputScoreOpacity),
            text = currentInputScore.toString(),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontSize = 40.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
            ),
        )
        Button(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .alpha(currentInputScoreOpacity),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_nano),
            ),
            elevation = null,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red)),
            onClick = onClearClicked,
            enabled = currentInputScore != 0,
        ) {
            ComposeText(
                text = stringResource(id = R.string.clear),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
        }
    }
}
