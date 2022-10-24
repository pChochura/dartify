package com.pointlessapps.dartify.compose.game.active.x01.ui.input.dart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pointlessapps.dartify.R
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.InputIconKey
import com.pointlessapps.dartify.compose.game.active.x01.ui.input.InputKey
import com.pointlessapps.dartify.compose.ui.components.ComposeExactGrid
import com.pointlessapps.dartify.compose.ui.components.ComposeText
import com.pointlessapps.dartify.compose.ui.components.defaultComposeTextStyle
import com.pointlessapps.dartify.compose.ui.modifiers.dragAfterLongPress
import com.pointlessapps.dartify.compose.ui.modifiers.rectBorder
import com.pointlessapps.dartify.compose.ui.utils.AbovePositionProvider
import com.pointlessapps.dartify.compose.utils.conditional
import com.pointlessapps.dartify.compose.utils.distance
import com.pointlessapps.dartify.compose.utils.scaledSp
import kotlin.math.floor

private const val MAX_POPUP_LONG_PRESS_DISTANCE = 300
private const val KEYBOARD_ROWS = 4
private const val KEYBOARD_COLUMNS = 5

@Composable
internal fun ColumnScope.DartInputKeyboard(
    onKeyClicked: (Int) -> Unit,
    onUndoClicked: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    var keyModifier by remember { mutableStateOf<Int?>(null) }

    Keyboard(
        keyModifier = keyModifier,
        onKeyClicked = {
            keyModifier = null
            onKeyClicked(it)
        },
        onKeyModifierResetRequested = {
            keyModifier = null
        },
    )

    BottomKeys(
        keyModifier = keyModifier,
        onKeyClicked = {
            keyModifier = null
            onKeyClicked(it)
        },
        onKeyModifierClicked = {
            keyModifier = if (keyModifier == it) null else it
        },
        onUndoClicked = {
            keyModifier = null
            onUndoClicked()
        },
        onDoneClicked = {
            keyModifier = null
            onDoneClicked()
        },
    )
}

@Suppress("MagicNumber")
@Composable
private fun ColumnScope.Keyboard(
    keyModifier: Int?,
    onKeyClicked: (Int) -> Unit,
    onKeyModifierResetRequested: () -> Unit,
) {
    var showPopup by remember { mutableStateOf<Popup?>(null) }
    var activePopupSelection by remember { mutableStateOf(PopupSelection(false)) }

    fun resetPopup() {
        showPopup = null
        onKeyModifierResetRequested()
        activePopupSelection = activePopupSelection.copy(show = false)
    }

    BoxWithConstraints(modifier = Modifier.weight(4f / 6)) {
        val width = constraints.maxWidth / KEYBOARD_COLUMNS.toFloat()
        val height = constraints.maxHeight / KEYBOARD_ROWS.toFloat()
        ComposeExactGrid(
            rows = KEYBOARD_ROWS,
            columns = KEYBOARD_COLUMNS,
            modifier = Modifier
                .fillMaxSize()
                .dragAfterLongPress(
                    key1 = width,
                    key2 = height,
                    onLongPressed = {
                        val x = floor(it.x / width).toInt()
                        val y = floor(it.y / height).toInt()
                        val value = keyValue(x, y)
                        showPopup = Popup(
                            popupOffset = Offset(x * width, y * height),
                            longPressOffset = Offset(x * width + width / 2f, it.y - height),
                            popupCenterX = x * width + width / 2f,
                            value = value,
                        )
                    },
                    onDrag = { change ->
                        showPopup?.also {
                            activePopupSelection = PopupSelection(
                                show = true,
                                selection = if (change.position.x < it.popupCenterX) {
                                    PopupSelection.Selection.LEFT
                                } else {
                                    PopupSelection.Selection.RIGHT
                                },
                            )
                            if (it.longPressOffset.distance(change.position) >= MAX_POPUP_LONG_PRESS_DISTANCE) {
                                activePopupSelection = activePopupSelection.copy(show = false)
                            }
                        }
                    },
                    onCancel = { activePopupSelection = activePopupSelection.copy(show = false) },
                    onEnd = {
                        showPopup?.let {
                            if (activePopupSelection.show) {
                                onKeyClicked(it.value * activePopupSelection.selection.multiplier)
                            }
                        }
                        resetPopup()
                    },
                ),
        ) { x, y ->
            val value = keyValue(x, y)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .conditional(showPopup != null && showPopup?.value != value) {
                        drawWithContent {
                            drawContent()
                            drawRect(color = Color.Black.copy(alpha = 0.4f))
                        }
                    },
            ) {
                InputKey(
                    label = when (keyModifier) {
                        2 -> "D$value"
                        3 -> "T$value"
                        else -> "$value"
                    },
                    onKeyClicked = {
                        if (showPopup == null) {
                            onKeyClicked(value * (keyModifier ?: 1))
                        }
                    },
                    hasSmallerFont = true,
                )
            }
        }

        showPopup?.let {
            LongClickPopup(
                value = it.value,
                active = activePopupSelection,
                position = it.popupOffset,
                onClicked = { value ->
                    onKeyClicked(value)
                    resetPopup()
                },
                onDismissRequest = { resetPopup() },
            )
        }
    }
}

@Suppress("MagicNumber")
@Composable
private fun ColumnScope.BottomKeys(
    keyModifier: Int?,
    onKeyClicked: (Int) -> Unit,
    onKeyModifierClicked: (Int) -> Unit,
    onUndoClicked: () -> Unit,
    onDoneClicked: () -> Unit,
) {
    ComposeExactGrid(
        rows = 2,
        columns = 3,
        modifier = Modifier.weight(2f / 6),
    ) { x, y ->
        when {
            x == 0 && y == 0 -> InputKey(
                label = "${if (keyModifier == 2) 50 else 25}",
                onKeyClicked = { onKeyClicked(if (keyModifier == 2) 50 else 25) },
                hasSmallerFont = true,
            )
            x == 1 && y == 0 -> ModifierKey(
                label = stringResource(id = R.string.modifier_double).uppercase(),
                selected = keyModifier == 2,
                onKeyClicked = { onKeyModifierClicked(2) },
            )
            x == 2 && y == 0 -> ModifierKey(
                label = stringResource(id = R.string.modifier_triple).uppercase(),
                selected = keyModifier == 3,
                onKeyClicked = { onKeyModifierClicked(3) },
            )
            x == 0 && y == 1 -> InputIconKey(
                icon = R.drawable.ic_undo,
                label = stringResource(id = R.string.undo),
                onKeyClicked = { onUndoClicked() },
            )
            x == 1 && y == 1 -> InputKey(
                label = stringResource(id = R.string.miss).uppercase(),
                onKeyClicked = { onKeyClicked(0) },
                hasSmallerFont = true,
            )
            x == 2 && y == 1 -> InputIconKey(
                icon = R.drawable.ic_done,
                label = stringResource(id = R.string.done),
                onKeyClicked = onDoneClicked,
                hasAccent = true,
            )
        }
    }
}

@Composable
private fun ModifierKey(label: String, selected: Boolean, onKeyClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .rectBorder(
                top = dimensionResource(id = R.dimen.score_button_border_width),
                left = dimensionResource(id = R.dimen.score_button_border_width),
                right = dimensionResource(id = R.dimen.score_button_border_width),
                color = MaterialTheme.colors.primary,
            )
            .background(
                if (selected) {
                    colorResource(id = R.color.red)
                } else {
                    MaterialTheme.colors.secondary
                },
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
                    fontSize = 18.scaledSp(),
                ),
            ),
        )
    }
}

@Composable
private fun LongClickPopup(
    value: Int,
    active: PopupSelection,
    position: Offset,
    onClicked: (Int) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val alignmentBias by animateFloatAsState(targetValue = active.selection.alignmentBias)
    val alpha by animateFloatAsState(targetValue = if (active.show) 1f else 0f)
    Popup(
        popupPositionProvider = AbovePositionProvider(position),
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .height(IntrinsicSize.Max)
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colors.background)
                .padding(dimensionResource(id = R.dimen.margin_medium)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(1 / 2f)
                    .alpha(alpha)
                    .align(BiasAlignment(alignmentBias, 0f))
                    .background(
                        MaterialTheme.colors.secondary,
                        shape = MaterialTheme.shapes.small,
                    ),
            )
            Row(
                modifier = Modifier.align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "${value.triple()}",
                            onClick = { onClicked(value.double()) },
                        )
                        .padding(dimensionResource(id = R.dimen.margin_small)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.margin_nano),
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_double),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary,
                    )
                    ComposeText(
                        text = "${value.double()}",
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center,
                            typography = MaterialTheme.typography.h1.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        ),
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f, false)
                        .clip(MaterialTheme.shapes.small)
                        .clickable(
                            role = Role.Button,
                            onClickLabel = "${value.triple()}",
                            onClick = { onClicked(value.triple()) },
                        )
                        .padding(dimensionResource(id = R.dimen.margin_small)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        dimensionResource(id = R.dimen.margin_nano),
                    ),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_triple),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onPrimary,
                    )
                    ComposeText(
                        text = "${value.triple()}",
                        textStyle = defaultComposeTextStyle().copy(
                            textColor = MaterialTheme.colors.onPrimary,
                            textAlign = TextAlign.Center,
                            typography = MaterialTheme.typography.h1.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        ),
                    )
                }
            }
        }
    }
}

private data class Popup(
    val popupOffset: Offset,
    val longPressOffset: Offset,
    val popupCenterX: Float,
    val value: Int,
)

private data class PopupSelection(val show: Boolean, val selection: Selection = Selection.LEFT) {
    enum class Selection(
        val alignmentBias: Float,
        val multiplier: Int,
    ) {
        LEFT(-1f, 2), RIGHT(1f, 3)
    }
}

@Suppress("MagicNumber")
private fun Int.double() = this * 2

@Suppress("MagicNumber")
private fun Int.triple() = this * 3

private fun keyValue(x: Int, y: Int) = y * KEYBOARD_COLUMNS + x + 1
