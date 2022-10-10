package com.pointlessapps.dartify.local.datasource.game.x01

import com.pointlessapps.dartify.datasource.game.x01.ScoreDataSource

@Suppress("MagicNumber")
internal class LocalScoreDataSource : ScoreDataSource {

    private val oneThrowRange = (1..20).toSet()

    private val oneThrowPossibleDoubleOutScores by lazy {
        (oneThrowRange.map { it * 2 } + 50).toSet()
    }
    private val oneThrowPossibleMasterOutScores by lazy {
        oneThrowPossibleDoubleOutScores + oneThrowRange.map { it * 3 }
    }
    private val oneThrowPossibleOutScores by lazy {
        oneThrowRange + oneThrowPossibleMasterOutScores
    }
    private val oneThrowPossibleScores by lazy {
        oneThrowPossibleOutScores + 0
    }

    private val twoThrowsPossibleScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleScores.map { second -> first + second }
        }.toSet()
    }
    private val twoThrowsPossibleOutScores by lazy {
        twoThrowsPossibleScores - 0
    }
    private val twoThrowsPossibleDoubleOutScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleDoubleOutScores.map { second -> first + second }
        }.toSet()
    }
    private val twoThrowsPossibleMasterOutScores by lazy {
        oneThrowPossibleScores.flatMap { first ->
            oneThrowPossibleMasterOutScores.map { second -> first + second }
        }.toSet()
    }

    private val threeThrowsPossibleScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleScores.map { third -> firstAndSecond + third }
        }.toSet()
    }
    private val threeThrowsPossibleOutScores by lazy {
        threeThrowsPossibleScores - 0
    }
    private val threeThrowsPossibleDoubleOutScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleDoubleOutScores.map { third -> firstAndSecond + third }
        }.toSet()
    }
    private val threeThrowsPossibleMasterOutScores by lazy {
        twoThrowsPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleMasterOutScores.map { third -> firstAndSecond + third }
        }.toSet()
    }

    private val twoThrowsTwoDoublesPossibleScores by lazy {
        oneThrowPossibleDoubleOutScores.flatMap { first ->
            oneThrowPossibleDoubleOutScores.map { second -> first + second }
        }.toSet()
    }

    private val threeThrowsOneDoublePossibleScores by lazy {
        oneThrowPossibleDoubleOutScores.flatMap { firstAndSecond ->
            oneThrowPossibleScores.map { third -> firstAndSecond + third }
        }.toSet()
    }
    private val threeThrowsTwoDoublesPossibleScores by lazy {
        twoThrowsTwoDoublesPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleScores.map { third -> firstAndSecond + third }
        }.toSet()
    }
    private val threeThrowsThreeDoublesPossibleScores by lazy {
        twoThrowsTwoDoublesPossibleScores.flatMap { firstAndSecond ->
            oneThrowPossibleDoubleOutScores.map { third -> firstAndSecond + third }
        }.toSet()
    }

    override fun getPossibleScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleScores
        2 -> twoThrowsPossibleScores
        3 -> threeThrowsPossibleScores
        else -> emptySet()
    }

    override fun getPossibleOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleOutScores
        2 -> twoThrowsPossibleOutScores
        3 -> threeThrowsPossibleOutScores
        else -> emptySet()
    }

    override fun getPossibleDoubleOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleDoubleOutScores
        2 -> twoThrowsPossibleDoubleOutScores
        3 -> threeThrowsPossibleDoubleOutScores
        else -> emptySet()
    }

    override fun getPossibleMasterOutScoresFor(numberOfThrows: Int) = when (numberOfThrows) {
        1 -> oneThrowPossibleMasterOutScores
        2 -> twoThrowsPossibleMasterOutScores
        3 -> threeThrowsPossibleMasterOutScores
        else -> emptySet()
    }

    override fun getPossibleDoubleScoresFor(numberOfDoubles: Int) = when (numberOfDoubles) {
        1 -> threeThrowsOneDoublePossibleScores
        2 -> threeThrowsTwoDoublesPossibleScores
        3 -> threeThrowsThreeDoublesPossibleScores
        else -> emptySet()
    }
}
