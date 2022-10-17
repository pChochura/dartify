package com.pointlessapps.dartify.datasource.game.x01.move

import com.pointlessapps.dartify.datasource.game.x01.move.model.PlayerScore

interface TurnDataSource {
    fun setStartingScore(score: Int)
    fun setPlayers(playerIds: List<Long>)
    fun addInput(
        playerId: Long,
        score: Int,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    )

    fun hasWonPreviousLeg(playerId: Long): Boolean
    fun hasNoInputs(playerId: Long): Boolean
    fun hasNoInputsInThisLeg(playerId: Long): Boolean

    fun revertLeg(playerId: Long)
    fun popInput(playerId: Long): Int

    fun finishLeg(winnerId: Long)
    fun finishSet(winnerId: Long)

    fun getWonSets(): Int
    fun getWonSets(playerId: Long): Int
    fun getWonLegs(): Int
    fun getWonLegs(playerId: Long): Int

    fun getScoreLeft(playerId: Long): Int

    fun getPlayerScores(): List<PlayerScore>
}
