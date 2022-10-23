package com.pointlessapps.dartify.compose.game.active.x01.ui.input

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.utils.conditional
import com.pointlessapps.dartify.compose.utils.scaledSp

@Composable
internal fun InputIconKey(
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
