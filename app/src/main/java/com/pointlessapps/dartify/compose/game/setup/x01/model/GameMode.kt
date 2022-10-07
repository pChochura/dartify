package com.pointlessapps.dartify.compose.game.setup.x01.model

import androidx.annotation.StringRes
import com.pointlessapps.dartify.R

internal enum class GameMode(
    @StringRes val label: Int,
    @StringRes val description: Int,
) {
    Straight(
        R.string.game_mode_straight,
        R.string.game_mode_straight_desc,
    ),
    Double(
        R.string.game_mode_double,
        R.string.game_mode_double_desc,
    ),
    Master(
        R.string.game_mode_master,
        R.string.game_mode_master_desc,
    ),
}
