package com.pointlessapps.dartify.compose.game.active.x01.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun NumberOfThrowsDialog(
    minNumberOfThrows: Int,
    onButtonClicked: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onDismissRequest,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.how_many_throws),
            icon = R.drawable.ic_darts,
            dismissible = false,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            repeat(3) {
                ComposeSimpleButton(
                    modifier = Modifier.weight(1f),
                    label = "${it + 1}",
                    onClick = { onButtonClicked(it + 1) },
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = MaterialTheme.colors.secondary,
                        icon = null,
                        orientation = ComposeSimpleButtonOrientation.Horizontal,
                        textStyle = defaultComposeTextStyle().copy(
                            textAlign = TextAlign.Center,
                            textColor = MaterialTheme.colors.onSecondary,
                            typography = MaterialTheme.typography.h1.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                            ),
                        ),
                        enabled = it + 1 >= minNumberOfThrows,
                    ),
                )
            }
        }
    }
}
