package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeHelpText(
    @StringRes text: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            dimensionResource(id = R.dimen.margin_tiny),
        ),
    ) {
        Icon(
            modifier = Modifier.size(dimensionResource(id = R.dimen.caption_icon_size)),
            painter = painterResource(id = R.drawable.ic_help),
            tint = MaterialTheme.colors.onBackground,
            contentDescription = null,
        )
        ComposeText(
            text = stringResource(id = text),
            textStyle = defaultComposeTextStyle().copy(
                textColor = MaterialTheme.colors.onBackground,
                typography = MaterialTheme.typography.subtitle1.copy(
                    fontSize = 10.sp,
                ),
            ),
        )
    }
}
