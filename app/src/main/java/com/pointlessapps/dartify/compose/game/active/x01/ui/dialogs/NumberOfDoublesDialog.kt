package com.pointlessapps.dartify.compose.game.active.x01.ui.dialogs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.ui.components.*

@Composable
internal fun NumberOfDoublesDialog(
    minNumberOfDoubles: Int,
    maxNumberOfDoubles: Int,
    onUndoLastMoveClicked: () -> Unit,
    onButtonClicked: (Int) -> Unit,
) {
    ComposeDialog(
        onDismissRequest = onUndoLastMoveClicked,
        dialogModel = defaultComposeDialogModel().copy(
            label = stringResource(id = R.string.how_many_throws_on_double),
            icon = R.drawable.ic_darts,
            dismissible = ComposeDialogDismissible.OnBackPress,
        ),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
            ),
        ) {
            repeat(4) {
                ComposeSimpleButton(
                    modifier = Modifier.weight(1f),
                    label = "$it",
                    onClick = { onButtonClicked(it) },
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
                        enabled = it in minNumberOfDoubles..maxNumberOfDoubles,
                    ),
                )
            }
        }

        Row(
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
                text = stringResource(id = R.string.how_many_throws_on_double_desc),
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onBackground,
                    typography = MaterialTheme.typography.subtitle1.copy(
                        fontSize = 10.sp,
                    ),
                ),
            )
        }

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
    }
}
