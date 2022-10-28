package com.pointlessapps.dartify.compose.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@Composable
internal fun ComposeText(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: ComposeTextStyle = defaultComposeTextStyle(),
) = ComposeText(
    annotatedString = AnnotatedString(text),
    modifier = modifier,
    textStyle = textStyle,
)

@Composable
internal fun ComposeText(
    annotatedString: AnnotatedString,
    modifier: Modifier = Modifier,
    textStyle: ComposeTextStyle = defaultComposeTextStyle(),
) = Text(
    modifier = modifier,
    text = annotatedString,
    style = textStyle.typography.copy(
        color = textStyle.textColor,
        textAlign = textStyle.textAlign,
    ),
    overflow = textStyle.textOverflow,
    maxLines = textStyle.maxLines,
)

@Composable
internal fun defaultComposeTextStyle() = ComposeTextStyle(
    textColor = MaterialTheme.colors.onPrimary,
    typography = MaterialTheme.typography.body1,
    textAlign = TextAlign.Start,
    textOverflow = TextOverflow.Visible,
    maxLines = Int.MAX_VALUE,
)

internal data class ComposeTextStyle(
    val textColor: Color,
    val textAlign: TextAlign,
    val typography: TextStyle,
    val textOverflow: TextOverflow,
    val maxLines: Int,
)
