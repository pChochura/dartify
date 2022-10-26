package com.pointlessapps.dartify.datasource.game.x01.score

interface ScoreDataSource {
    /**
     * Returns all possible scores that a player can score after [numberOfThrows] throws
     * (including 0)
     */
    fun getPossibleScoresFor(numberOfThrows: Int): Set<Int>

    /**
     * Returns all possible scores that a player can score after [numberOfThrows] throws
     * that ends with a checkout
     */
    fun getPossibleOutScoresFor(numberOfThrows: Int): Set<Int>

    /**
     * Returns all possible scores that a player can score after [numberOfThrows] throws
     * that ends with a checkout with a last shot on a double
     */
    fun getPossibleDoubleOutScoresFor(numberOfThrows: Int): Set<Int>

    /**
     * Returns all possible scores that a player can score after [numberOfThrows] throws
     * that ends with a checkout with a last shot on a double or treble
     */
    fun getPossibleMasterOutScoresFor(numberOfThrows: Int): Set<Int>

    /**
     * Checks if the [score] is possible to be checked out after [numberOfThrows] throws
     * that contain [numberOfDoubles] shots on double
     */
    fun isScorePossibleToDoubleOutWith(
        score: Int,
        numberOfThrows: Int,
        numberOfDoubles: Int,
    ): Boolean
}
