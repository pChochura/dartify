package com.pointlessapps.dartify.datasource.game.x01

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
     * Returns all possible scores that a player can score after [numberOfThrows] throws
     * that contains [numberOfDoubles] shots on double
     */
    fun getPossibleDoubleScoresFor(numberOfThrows: Int, numberOfDoubles: Int): Set<Int>
}
