package com.pointlessapps.dartify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.core.view.WindowCompat
import com.pointlessapps.dartify.compose.NavHost
import com.pointlessapps.dartify.compose.ui.components.ComposeSnackbar
import com.pointlessapps.dartify.compose.ui.components.ComposeSnackbarHostState
import com.pointlessapps.dartify.compose.ui.theme.ProjectTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            ProjectTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()
                val composeSnackbarHostState = ComposeSnackbarHostState(
                    object : ComposeSnackbarHostState.SnackbarHostListener {
                        override fun showSnackbar(
                            message: Int,
                            actionLabel: Int?,
                            actionCallback: (() -> Unit)?,
                            duration: SnackbarDuration,
                        ) {
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = context.getString(message),
                                    actionLabel = actionLabel?.let { context.getString(it) },
                                    duration = duration,
                                )
                                if (result == SnackbarResult.ActionPerformed) {
                                    actionCallback?.invoke()
                                }
                            }
                        }
                    },
                )

                CompositionLocalProvider(
                    LocalTextSelectionColors provides TextSelectionColors(
                        handleColor = MaterialTheme.colors.onPrimary,
                        backgroundColor = MaterialTheme.colors.onPrimary.copy(alpha = 0.4f),
                    ),
                    LocalSnackbarHostState provides composeSnackbarHostState,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colors.background,
                    ) {
                        NavHost()

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .systemBarsPadding()
                                .imePadding()
                                .padding(dimensionResource(id = R.dimen.margin_big)),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            SnackbarHost(hostState = snackbarHostState) {
                                ComposeSnackbar(
                                    message = it.message,
                                    actionLabel = it.actionLabel,
                                    actionCallback = { it.performAction() },
                                    onDismissRequest = { it.dismiss() },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

internal val LocalSnackbarHostState = compositionLocalOf<ComposeSnackbarHostState> {
    error("No AmnesiaSnackbarHostState")
}

