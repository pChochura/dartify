package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.utils.conditional
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun ScoreStatisticEntry(
    @DrawableRes icon: Int,
    label: String,
    value: String,
    backgroundColor: Color? = null,
) {
    Row(
        modifier = Modifier
            .border(
                width = dimensionResource(id = R.dimen.player_score_border_width),
                color = MaterialTheme.colors.primary,
                shape = CircleShape,
            )
            .conditional(backgroundColor != null) {
                background(
                    color = backgroundColor!!,
                    shape = CircleShape,
                )
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.margin_tiny),
                vertical = dimensionResource(id = R.dimen.margin_nano),
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.player_score_icon_size)),
            painter = painterResource(id = icon),
            tint = MaterialTheme.colors.onSecondary,
            contentDescription = null,
        )
        ComposeText(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.margin_nano)),
            text = label,
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.subtitle1.copy(
                    fontSize = 8.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
                textOverflow = TextOverflow.Ellipsis,
                maxLines = 1,
            ),
        )
        ComposeText(
            modifier = Modifier.padding(start = dimensionResource(id = R.dimen.margin_nano)),
            text = value,
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onSecondary,
                typography = MaterialTheme.typography.subtitle1.copy(
                    fontSize = 10.scaledSp(),
                    fontWeight = FontWeight.Bold,
                ),
                textOverflow = TextOverflow.Clip,
                maxLines = 1,
            ),
        )
    }
}
