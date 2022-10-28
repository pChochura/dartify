package com.pointlessapps.dartify.compose.game.active.x01.ui.input.turn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore
import com.pointlessapps.dartify.compose.game.active.x01.model.score
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun TurnInputScore(
    finishSuggestion: String?,
    currentInputScore: InputScore?,
    onClearClicked: () -> Unit,
) {
    val score = remember(currentInputScore) { currentInputScore.score() }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                vertical = dimensionResource(id = R.dimen.margin_small),
            ),
    ) {
        AnimatedVisibility(
            modifier = Modifier.align(Alignment.CenterStart),
            visible = finishSuggestion != null,
        ) {
            ComposeText(
                text = finishSuggestion.toString(),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onSecondary,
                    typography = MaterialTheme.typography.h1.copy(
                        fontSize = 16.scaledSp(),
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )
        }
        ComposeText(
            modifier = Modifier.align(Alignment.Center),
            text = "$score",
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h1.copy(
                    fontSize = 40.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
            ),
        )
        Button(
            modifier = Modifier.align(Alignment.CenterEnd),
            contentPadding = PaddingValues(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_nano),
            ),
            elevation = null,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.red)),
            onClick = onClearClicked,
            enabled = score != 0,
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
