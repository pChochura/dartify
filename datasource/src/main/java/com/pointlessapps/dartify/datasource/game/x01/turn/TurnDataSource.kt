package com.pointlessapps.dartify.datasource.game.x01.turn

import com.pointlessapps.dartify.datasource.database.game.x01.model.GameX01Input
import com.pointlessapps.dartify.datasource.game.x01.turn.model.InputScore
import com.pointlessapps.dartify.datasource.game.x01.turn.model.PlayerScore

interface TurnDataSource {
    /**
     * Sets up the score handlers for the players and loads all previous inputs
     */
    fun load(startingScore: Int, playerIds: List<Long>, inputs: List<GameX01Input>)

    /**
     * Sets up the score handlers for the players
     */
    fun setup(startingScore: Int, playerIds: List<Long>)

    /**
     * Performs an insertion of the score to the player's account marked by [playerId]
     */
    fun addInput(
        playerId: Long,
        score: InputScore,
        numberOfThrows: Int,
        numberOfThrowsOnDouble: Int,
    )

    /**
     * Returns true if the player with the [playerId] has won the previous leg
     */
    fun hasWonPreviousLeg(playerId: Long): Boolean

    /**
     * Returns true if the player with the [playerId] has no inputs in the game
     */
    fun hasNoInputs(playerId: Long): Boolean

    /**
     * Returns true if the player with the [playerId] has no inputs in this leg
     */
    fun hasNoInputsInThisLeg(playerId: Long): Boolean

    /**
     * Reverts a leg for the player with the [playerId] by removing inputs from the
     * history and setting them as the current ones
     */
    fun revertLeg(playerId: Long)

    /**
     * If the player with the [playerId] has inputs in this leg, it returns the score from the
     * previous throw and removes it from the history. Otherwise if the player has previous leg
     * inputs, it removes them from the history, marks them as the current ones and remove the
     * the last throw, returning the score back
     */
    fun popInput(playerId: Long): InputScore?

    /**
     * Saves the current leg's inputs for all the players to the history
     * and clears the current inputs
     */
    fun finishLeg(winnerId: Long)

    /**
     * Saves the current leg's inputs for all the players to the history
     * and clears the current inputs
     */
    fun finishSet(winnerId: Long)

    /**
     * Returns the number of finished sets collectively from all the players
     */
    fun getWonSets(): Int

    /**
     * Returns the number of won sets by the player with the [playerId]
     */
    fun getWonSets(playerId: Long): Int

    /**
     * Returns the number of finished legs collectively from all the players in the current set
     */
    fun getWonLegs(): Int

    /**
     * Returns the number of won legs by the player with the [playerId] in the current set
     */
    fun getWonLegs(playerId: Long): Int

    /**
     * Returns the number of finished legs collectively from all the players in the game
     */
    fun getNumberOfLegsPlayed(): Int

    /**
     * Returns the score left for the player with the [playerId]
     */
    fun getScoreLeft(playerId: Long): Int

    /**
     * Returns the scores for all of the players
     */
    fun getPlayerScores(): List<PlayerScore>

    /**
     * Returns the inputs for all of the players
     */
    fun getAllInputsHistory(): List<GameX01Input>
}
