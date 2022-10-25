package com.pointlessapps.dartify.domain.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.TurnDataSource
import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import com.pointlessapps.dartify.datasource.game.x01.move.model.PlayerScore
import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.game.x01.model.Player
import com.pointlessapps.dartify.domain.game.x01.score.model.GameMode
import com.pointlessapps.dartify.domain.game.x01.turn.mappers.toInputScore
import com.pointlessapps.dartify.domain.game.x01.turn.model.CurrentState
import com.pointlessapps.dartify.domain.game.x01.turn.model.DoneTurnEvent
import com.pointlessapps.dartify.domain.game.x01.turn.model.MatchResolutionStrategy
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.mockk.*

internal class TurnRepositoryTest : AnnotationSpec() {

    private val dataSource: TurnDataSource = mockk()
    private val repository: TurnRepository = TurnRepositoryImpl(dataSource)

    @BeforeEach
    fun beforeEach() {
        clearMocks(dataSource)
    }

    private fun setupRepository(
        players: List<Player>,
        startingScore: Int,
        inMode: GameMode,
    ): CurrentState {
        every { dataSource.setup(startingScore, players.map(Player::id)) } just Runs
        return repository.setup(
            players = players,
            startingScore = startingScore,
            inMode = inMode,
            numberOfSets = 1,
            numberOfLegs = 3,
            matchResolutionStrategy = MatchResolutionStrategy.FirstTo,
        )
    }

    @Test
    fun `Test calling setup`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val startingScore = 501
        val inMode = GameMode.Straight
        every { dataSource.getPlayerScores() } returns emptyList()
        val currentState = setupRepository(players, startingScore, inMode)

        currentState.score should beNull()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs players.first()

        verify(exactly = 1) { dataSource.setup(startingScore, players.map(Player::id)) }
    }

    @Test
    fun `Test calling addInput`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val score = InputScore.Turn(25)
        val numberOfThrows = 3
        val numberOfThrowsOnDouble = 1
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        every {
            dataSource.addInput(players.first().id, score, numberOfThrows, numberOfThrowsOnDouble)
        } just Runs
        repository.addInput(
            score = score.toInputScore(),
            numberOfThrows = numberOfThrows,
            numberOfThrowsOnDouble = numberOfThrowsOnDouble,
        )

        verify(exactly = 1) {
            dataSource.addInput(players.first().id, score, numberOfThrows, numberOfThrowsOnDouble)
        }
    }

    @Test
    fun `Test calling undoTurn (reverting only a throw)`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val previousPlayer = players[0.prevPlayerIndex(players.size)]
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        val inputScore = InputScore.Turn(100)

        every { dataSource.hasNoInputs(any()) } returns false
        every { dataSource.hasNoInputsInThisLeg(any()) } returns false
        every { dataSource.popInput(any()) } returns inputScore
        every { dataSource.getWonSets() } returns 0
        every { dataSource.getWonLegs() } returns 0
        val currentState = repository.undoTurn()

        currentState.score shouldBe inputScore.toInputScore()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs previousPlayer

        verify(exactly = 1) { dataSource.hasNoInputs(any()) }
        verify(exactly = 1) { dataSource.hasNoInputsInThisLeg(any()) }
        verify(exactly = 1) { dataSource.popInput(any()) }
        verify(exactly = 1) { dataSource.getWonSets() }
        verify(exactly = 1) { dataSource.getWonLegs() }
    }

    @Test
    fun `Test calling undoTurn (no possible reverts)`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val currentPlayer = players.first()
        val previousPlayer = players[0.prevPlayerIndex(players.size)]
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        every { dataSource.hasNoInputs(previousPlayer.id) } returns true
        every { dataSource.getWonSets() } returns 0
        every { dataSource.getWonLegs() } returns 0
        every { dataSource.getPlayerScores() } returns emptyList()
        val currentState = repository.undoTurn()

        currentState.score should beNull()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs currentPlayer

        verify(exactly = 1) { dataSource.hasNoInputs(previousPlayer.id) }
        verify(exactly = 1) { dataSource.getWonSets() }
        verify(exactly = 1) { dataSource.getWonLegs() }
    }

    @Test
    fun `Test calling undoTurn (reverting the set or leg) - current player won previous leg`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val currentPlayer = players.first()
        val previousPlayer = players[0.prevPlayerIndex(players.size)]
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        val inputScore = InputScore.Turn(100)

        every { dataSource.hasNoInputs(any()) } returns false
        every { dataSource.hasNoInputsInThisLeg(previousPlayer.id) } returns true
        every { dataSource.hasWonPreviousLeg(currentPlayer.id) } returns true
        every { dataSource.revertLeg(any()) } just Runs
        every { dataSource.popInput(currentPlayer.id) } returns inputScore
        every { dataSource.getWonSets() } returns 0
        every { dataSource.getWonLegs() } returns 0
        val currentState = repository.undoTurn()

        currentState.score shouldBe inputScore.toInputScore()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs currentPlayer

        verify(exactly = 1) { dataSource.hasNoInputs(any()) }
        verify(exactly = 1) { dataSource.hasNoInputsInThisLeg(any()) }
        players.forEach {
            if (it.id != currentPlayer.id) {
                verify(exactly = 1) { dataSource.revertLeg(it.id) }
            }
        }
        verify(exactly = 1) { dataSource.popInput(any()) }
        verify(exactly = 1) { dataSource.getWonSets() }
        verify(exactly = 1) { dataSource.getWonLegs() }
    }

    @Test
    fun `Test calling undoTurn (reverting the set or leg) - current player hasn't won previous leg`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val currentPlayer = players.first()
        val previousPlayer = players[0.prevPlayerIndex(players.size)]
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        val inputScore = InputScore.Turn(100)

        every { dataSource.hasNoInputs(any()) } returns false
        every { dataSource.hasNoInputsInThisLeg(previousPlayer.id) } returns true
        every { dataSource.hasWonPreviousLeg(currentPlayer.id) } returns false
        every { dataSource.revertLeg(any()) } just Runs
        every { dataSource.popInput(previousPlayer.id) } returns inputScore
        every { dataSource.getWonSets() } returns 0
        every { dataSource.getWonLegs() } returns 0
        val currentState = repository.undoTurn()

        currentState.score shouldBe inputScore.toInputScore()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs previousPlayer

        verify(exactly = 1) { dataSource.hasNoInputs(any()) }
        verify(exactly = 1) { dataSource.hasNoInputsInThisLeg(any()) }
        players.forEach {
            if (it.id != previousPlayer.id) {
                verify(exactly = 1) { dataSource.revertLeg(it.id) }
            }
        }
        verify(exactly = 1) { dataSource.popInput(any()) }
        verify(exactly = 1) { dataSource.getWonSets() }
        verify(exactly = 1) { dataSource.getWonLegs() }
    }

    @Test
    fun `Test calling nextTurn`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        val nextPlayer = players[0.nextPlayerIndex(players.size)]
        every { dataSource.getPlayerScores() } returns emptyList()
        setupRepository(players, 501, GameMode.Straight)

        every { dataSource.getWonSets() } returns 0
        every { dataSource.getWonLegs() } returns 0
        val currentState = repository.nextTurn()

        currentState.score should beNull()
        currentState.set shouldBeExactly 1
        currentState.leg shouldBeExactly 1
        currentState.player shouldBeSameInstanceAs nextPlayer

        verify(exactly = 1) { dataSource.getWonSets() }
        verify(exactly = 1) { dataSource.getWonLegs() }
    }

    @Test
    fun `Test calling doneTurn - AddInput expected`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        every { dataSource.getPlayerScores() } returns listOf(
            PlayerScore(
                numberOfWonSets = 0,
                numberOfWonLegs = 0,
                doublePercentage = 0f,
                maxScore = 0,
                averageScore = 0f,
                numberOfDarts = 0,
                scoreLeft = 100,
                lastScore = 0,
                playerId = 1L,
            ),
        )
        setupRepository(players, 501, GameMode.Straight)

        val doneTurnEvent = repository.doneTurn(12, false, 0, emptyMap())

        doneTurnEvent shouldBe DoneTurnEvent.AddInput

        verify(exactly = 2) { dataSource.getPlayerScores() }
    }

    @Test
    fun `Test calling doneTurn - AskForNumberOfDoubles expected, maxNumberOfDoublesForThreeThrows = 2`() {
        val maxNumberOfDoublesForThreeThrows = 2
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        every { dataSource.getPlayerScores() } returns listOf(
            PlayerScore(
                numberOfWonSets = 0,
                numberOfWonLegs = 0,
                doublePercentage = 0f,
                maxScore = 0,
                averageScore = 0f,
                numberOfDarts = 0,
                scoreLeft = 100,
                lastScore = 0,
                playerId = 1L,
            ),
        )
        setupRepository(players, 501, GameMode.Straight)

        val doneTurnEvent = repository.doneTurn(
            50,
            true,
            0,
            mapOf(DEFAULT_NUMBER_OF_THROWS to maxNumberOfDoublesForThreeThrows),
        )

        doneTurnEvent should beInstanceOf<DoneTurnEvent.AskForNumberOfDoubles>()

        (doneTurnEvent as DoneTurnEvent.AskForNumberOfDoubles).should {
            it.maxNumberOfDoublesForThreeThrows shouldBeExactly maxNumberOfDoublesForThreeThrows
        }

        verify(exactly = 2) { dataSource.getPlayerScores() }
    }

    @Test
    fun `Test calling doneTurn - AskForNumberOfDoubles expected, default maxNumberOfDoublesForThreeThrows`() {
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        every { dataSource.getPlayerScores() } returns listOf(
            PlayerScore(
                numberOfWonSets = 0,
                numberOfWonLegs = 0,
                doublePercentage = 0f,
                maxScore = 0,
                averageScore = 0f,
                numberOfDarts = 0,
                scoreLeft = 100,
                lastScore = 0,
                playerId = 1L,
            ),
        )
        setupRepository(players, 501, GameMode.Straight)

        val doneTurnEvent = repository.doneTurn(50, true, 0, emptyMap())

        doneTurnEvent should beInstanceOf<DoneTurnEvent.AskForNumberOfDoubles>()

        (doneTurnEvent as DoneTurnEvent.AskForNumberOfDoubles).should {
            it.maxNumberOfDoublesForThreeThrows shouldBeExactly 1
        }

        verify(exactly = 2) { dataSource.getPlayerScores() }
    }

    @Test
    fun `Test calling doneTurn - AskForNumberOfThrows expected`() {
        val scoreLeft = 100
        val minNumberOfThrows = 2
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        every { dataSource.getPlayerScores() } returns listOf(
            PlayerScore(
                numberOfWonSets = 0,
                numberOfWonLegs = 0,
                doublePercentage = 0f,
                maxScore = 0,
                averageScore = 0f,
                numberOfDarts = 0,
                scoreLeft = scoreLeft,
                lastScore = 0,
                playerId = 1L,
            ),
        )
        setupRepository(players, 501, GameMode.Straight)

        val doneTurnEvent = repository.doneTurn(scoreLeft, false, minNumberOfThrows, emptyMap())

        doneTurnEvent should beInstanceOf<DoneTurnEvent.AskForNumberOfThrows>()

        (doneTurnEvent as DoneTurnEvent.AskForNumberOfThrows).should {
            it.minNumberOfThrows shouldBeExactly minNumberOfThrows
        }

        verify(exactly = 2) { dataSource.getPlayerScores() }
    }

    @Test
    fun `Test calling doneTurn - AskForNumberOfThrowsAndDoubles expected`() {
        val scoreLeft = 100
        val minNumberOfThrows = 2
        val maxNumberOfDoubles = mapOf(1 to 1, 2 to 3, 3 to 3)
        val players = listOf(
            Player(name = "player 1", outMode = GameMode.Double, id = 1L),
            Player(name = "player 2", outMode = GameMode.Double, id = 2L),
        )
        every { dataSource.getPlayerScores() } returns listOf(
            PlayerScore(
                numberOfWonSets = 0,
                numberOfWonLegs = 0,
                doublePercentage = 0f,
                maxScore = 0,
                averageScore = 0f,
                numberOfDarts = 0,
                scoreLeft = scoreLeft,
                lastScore = 0,
                playerId = 1L,
            ),
        )
        setupRepository(players, 501, GameMode.Straight)

        val doneTurnEvent = repository.doneTurn(
            scoreLeft,
            true,
            minNumberOfThrows,
            maxNumberOfDoubles,
        )

        doneTurnEvent should beInstanceOf<DoneTurnEvent.AskForNumberOfThrowsAndDoubles>()

        (doneTurnEvent as DoneTurnEvent.AskForNumberOfThrowsAndDoubles).should {
            it.minNumberOfThrows shouldBeExactly minNumberOfThrows
            it.maxNumberOfDoubles shouldBeSameInstanceAs maxNumberOfDoubles
        }

        verify(exactly = 2) { dataSource.getPlayerScores() }
    }

    private fun Int.nextPlayerIndex(size: Int) = (this + 1) % size
    private fun Int.prevPlayerIndex(size: Int) = (this + size - 1) % size
}
