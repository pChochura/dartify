package com.pointlessapps.dartify.datasource.database.model

enum class GameMode {
    Straight, Double, Master;

    companion object {
        val DEFAULT_OUT_MODE = Double
    }
}
