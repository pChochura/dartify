package com.pointlessapps.dartify.datasource.game.x01

interface ScoreDataSource {
    fun getPossibleScoresFor(numberOfThrows: Int): Set<Int>
    fun getPossibleOutScoresFor(numberOfThrows: Int): Set<Int>
    fun getPossibleDoubleOutScoresFor(numberOfThrows: Int): Set<Int>
    fun getPossibleMasterOutScoresFor(numberOfThrows: Int): Set<Int>
    fun getPossibleDoubleScoresFor(numberOfDoubles: Int): Set<Int>
}
