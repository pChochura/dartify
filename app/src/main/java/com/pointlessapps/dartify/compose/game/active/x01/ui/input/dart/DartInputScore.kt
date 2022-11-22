package com.pointlessapps.dartify.compose.game.active.x01.ui.input.dart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.model.InputScore
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.utils.extensions.scaledSp

@Composable
internal fun DartInputScore(
    finishSuggestion: String?,
    currentInputScore: InputScore?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_semi_big),
                vertical = dimensionResource(id = R.dimen.margin_small),
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_small),
        ),
    ) {
        if (currentInputScore is InputScore.Turn && currentInputScore.score != 0) {
            DartSingleInputScore(
                value = "${currentInputScore.score}",
                alpha = 1f,
                backgroundColor = MaterialTheme.colors.secondary,
            )
        } else {
            DartTripleInputScore(finishSuggestion, currentInputScore as? InputScore.Dart)
        }
    }
}

@Composable
private fun RowScope.DartTripleInputScore(
    finishSuggestion: String?,
    currentInputScore: InputScore.Dart?,
) {
    val finishSuggestionSplit = remember { finishSuggestion?.split(" ") }
    repeat(3) { index ->
        val score = currentInputScore?.scores?.getOrNull(index)
        val checkout = finishSuggestionSplit?.getOrNull(index)
        val alpha = if (score != null || checkout != null) {
            1f
        } else {
            0f
        }
        val backgroundColor = if (currentInputScore?.scores?.getOrNull(index) != null) {
            MaterialTheme.colors.secondary
        } else {
            MaterialTheme.colors.background
        }
        DartSingleInputScore(
            value = score?.toString() ?: checkout ?: "",
            alpha = alpha,
            backgroundColor = backgroundColor,
        )
    }
}

@Composable
private fun RowScope.DartSingleInputScore(
    value: String,
    alpha: Float,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = dimensionResource(id = R.dimen.input_button_border_width),
                color = MaterialTheme.colors.secondary,
                shape = CircleShape,
            )
            .padding(
                vertical = dimensionResource(id = R.dimen.margin_tiny),
                horizontal = dimensionResource(id = R.dimen.margin_medium),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            modifier = Modifier
                .alpha(1f - alpha)
                .size(dimensionResource(id = R.dimen.button_icon_size)),
            painter = painterResource(id = R.drawable.ic_dart),
            tint = MaterialTheme.colors.onSecondary,
            contentDescription = null,
        )

        ComposeText(
            modifier = Modifier.alpha(alpha),
            text = value,
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.h2.copy(
                    fontSize = 20.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            ),
        )
    }
}
