package com.pointlessapps.dartify.compose.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.pointlessapps.dartify.R

@Composable
internal fun ComposeSnackbar(
    message: String,
    actionLabel: String?,
    actionCallback: (() -> Unit)?,
    onDismissRequest: () -> Unit,
) {
    Snackbar(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDismissRequest),
        shape = MaterialTheme.shapes.small,
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(id = R.dimen.margin_small),
                Alignment.CenterHorizontally,
            ),
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                painter = painterResource(id = R.drawable.ic_warning),
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = null,
            )
            ComposeText(
                modifier = Modifier.weight(1f),
                text = message,
                textStyle = defaultComposeTextStyle().copy(
                    textColor = MaterialTheme.colors.onPrimary,
                    typography = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                ),
            )

            if (actionLabel != null) {
                ComposeText(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.small)
                        .clickable { actionCallback?.invoke() }
                        .padding(dimensionResource(id = R.dimen.margin_tiny)),
                    text = actionLabel.uppercase(),
                    textStyle = defaultComposeTextStyle().copy(
                        textColor = MaterialTheme.colors.secondary,
                        typography = MaterialTheme.typography.body1.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    ),
                )
            }
        }
    }
}

internal class ComposeSnackbarHostState(private val onShowSnackbarListener: SnackbarHostListener) {

    fun showSnackbar(
        @StringRes message: Int,
        @StringRes actionLabel: Int? = null,
        actionCallback: (() -> Unit)? = null,
        dismissCallback: (() -> Unit)? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
    ) = onShowSnackbarListener.showSnackbar(
        message = message,
        actionLabel = actionLabel,
        actionCallback = actionCallback,
        dismissCallback = dismissCallback,
        duration = duration,
    )

    interface SnackbarHostListener {
        fun showSnackbar(
            @StringRes message: Int,
            @StringRes actionLabel: Int?,
            actionCallback: (() -> Unit)?,
            dismissCallback: (() -> Unit)?,
            duration: SnackbarDuration,
        )
    }
}
