package com.pointlessapps.dartify.compose.game.active.x01.ui.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun InputKey(
    label: String,
    onKeyClicked: () -> Unit,
    hasSmallerFont: Boolean = false,
) {
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
                    fontSize = (if (hasSmallerFont) 18 else 24).scaledSp(),
                ),
            ),
        )
    }
}
