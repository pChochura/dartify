package com.pointlessapps.dartify.compose.game.active.x01.ui.input.dart

import androidx.annotation.Size
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun DartInputScore(
    finishSuggestion: String?,
    @Size(max = 3) currentInputScore: List<Int>,
) {
    val finishSuggestionSplit = remember { finishSuggestion?.split(" ") }
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
        repeat(3) { index ->
            val score = currentInputScore.getOrNull(index)
            val checkout = finishSuggestionSplit?.getOrNull(index)
            val alpha = if (score != null || checkout != null) {
                1f
            } else {
                0f
            }
            val backgroundColor = if (currentInputScore.getOrNull(index) != null) {
                MaterialTheme.colors.secondary
            } else {
                MaterialTheme.colors.background
            }
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
                    text = score?.toString() ?: checkout ?: "",
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
    }
}
