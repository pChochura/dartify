package com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
    onUndoLastMoveClicked: () -> Unit,
    onDoneClicked: (Int) -> Unit,
) {
    var numberOfThrows by remember { mutableStateOf(minNumberOfThrows) }

    ComposeDialog(
        onDismissRequest = onUndoLastMoveClicked,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.how_many_throws),
            icon = R.drawable.ic_darts,
            dismissible = ComposeDialogDismissible.OnBackPress,
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
                    onClick = { numberOfThrows = it + 1 },
                    simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                        backgroundColor = if (numberOfThrows == it + 1) {
                            colorResource(id = R.color.red)
                        } else {
                            MaterialTheme.colors.secondary
                        },
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

        Column(
            verticalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_tiny),
            ),
        ) {
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.undo_last_move),
                onClick = onUndoLastMoveClicked,
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = MaterialTheme.colors.secondary,
                    icon = R.drawable.ic_undo,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
            ComposeSimpleButton(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(id = R.string.done),
                onClick = {
                    if (validateNumberOfThrows(minNumberOfThrows, numberOfThrows)) {
                        onDoneClicked(numberOfThrows)
                    }
                },
                simpleButtonModel = defaultComposeSimpleButtonModel().copy(
                    backgroundColor = colorResource(id = R.color.red),
                    icon = R.drawable.ic_done,
                    orientation = ComposeSimpleButtonOrientation.Horizontal,
                ),
            )
        }
    }
}

private fun validateNumberOfThrows(minNumberOfThrows: Int, numberOfThrows: Int) =
    numberOfThrows >= minNumberOfThrows
