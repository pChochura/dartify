package com.pointlessapps.dartify.datasource.database.players.model

enum class GameMode {
    Straight, Double, Master;

    companion object {
        val DEFAULT = Double
    }
}