package com.pointlessapps.dartify.domain.model

enum class GameMode {
    Straight, Double, Master;

    companion object {
        val DEFAULT_OUT_MODE = Double
        val DEFAULT_IN_MODE = Straight
    }
}
