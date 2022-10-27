package com.pointlessapps.dartify.local.datasource.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.TurnDataSource
import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.datasource.game.x01.move.model.PlayerScore
import com.pointlessapps.dartify.errors.game.x01.move.NotExistentPlayerException

internal class LocalTurnDataSource : TurnDataSource {

    private val playerScoreHandlers = mutableMapOf<Long, PlayerScoreHandler>()

    private fun getScoreHandler(playerId: Long) =
        playerScoreHandlers[playerId] ?: throw NotExistentPlayerException(playerId)

    override fun setup(startingScore: Int, playerIds: List<Long>) {
        playerScoreHandlers.clear()
        playerIds.forEach {
            playerScoreHandlers[it] = PlayerScoreHandler(startingScore)
        }
    }

    override fun addInput(
        playerId: Long,
        score: InputScore,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    ) {
        if (numberOfThrowsOnDouble > numberOfThrows) {
            throw IllegalArgumentException(
                "numberOfThrowsOnDouble ($numberOfThrowsOnDouble) cannot be " +
                        "bigger than numberOfThrows ($numberOfThrows)",
            )
        }

        val handler = getScoreHandler(playerId)
        handler.addInput(score, numberOfThrows, numberOfThrowsOnDouble)
    }

    override fun hasWonPreviousLeg(playerId: Long) =
        getScoreHandler(playerId).hasWonPreviousLeg()

    override fun hasNoInputs(playerId: Long) = getScoreHandler(playerId).hasNoInputs()

    override fun hasNoInputsInThisLeg(playerId: Long) =
        getScoreHandler(playerId).numberOfDarts == 0

    override fun revertLeg(playerId: Long) {
        getScoreHandler(playerId).markLegAsReverted()
    }

    override fun popInput(playerId: Long) = getScoreHandler(playerId).popInput()

    override fun finishSet(winnerId: Long) {
        playerScoreHandlers.forEach { (id, handler) ->
            handler.markSetAsFinished(winnerId == id)
        }
    }

    override fun finishLeg(winnerId: Long) {
        playerScoreHandlers.forEach { (id, handler) ->
            handler.markLegAsFinished(winnerId == id)
        }
    }

    override fun getWonSets() = playerScoreHandlers.values.sumOf { it.wonSets }
    override fun getWonSets(playerId: Long) = getScoreHandler(playerId).wonSets

    override fun getWonLegs() = playerScoreHandlers.values.sumOf { it.wonLegs }
    override fun getWonLegs(playerId: Long) = getScoreHandler(playerId).wonLegs

    override fun getNumberOfLegsPlayed() = playerScoreHandlers.values.sumOf { it.allWonLegs }

    override fun getScoreLeft(playerId: Long) = getScoreHandler(playerId).scoreLeft

    override fun getPlayerScores() = playerScoreHandlers.map { (id, score) ->
        PlayerScore(
            numberOfWonSets = score.wonSets,
            numberOfWonLegs = score.wonLegs,
            doublePercentage = score.doublePercentage,
            maxScore = score.max,
            averageScore = score.average,
            numberOfDarts = score.numberOfDarts,
            scoreLeft = score.scoreLeft,
            lastScore = score.lastScore,
            playerId = id,
        )
    }
}
