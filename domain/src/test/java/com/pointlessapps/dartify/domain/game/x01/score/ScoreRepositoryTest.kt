package com.pointlessapps.dartify.domain.game.x01.score

import com.pointlessapps.dartify.datasource.game.x01.score.ScoreDataSource
import com.pointlessapps.dartify.domain.game.x01.DEFAULT_NUMBER_OF_THROWS
import com.pointlessapps.dartify.domain.model.GameMode
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

internal class ScoreRepositoryTest : AnnotationSpec() {

    private val dataSource: ScoreDataSource = mockk()
    private val repository: ScoreRepository = ScoreRepositoryImpl(dataSource)

    @BeforeEach
    fun beforeEach() {
        clearMocks(dataSource)
    }

    @Test
    fun `Test validating score - valid check in (straight in)`() {
        every { dataSource.getPossibleScoresFor(DEFAULT_NUMBER_OF_THROWS) } returns setOf(1)
        val validScore = repository.validateScore(
            score = 1,
            scoreLeft = 500,
            startingScore = 501,
            numberOfThrows = DEFAULT_NUMBER_OF_THROWS,
            inMode = GameMode.Straight,
            outMode = GameMode.Double,
        )

        validScore shouldBe true
        verify(exactly = 1) { dataSource.getPossibleScoresFor(DEFAULT_NUMBER_OF_THROWS) }
    }

    @Test
    fun `Test validating score - invalid check in (double in)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(1)
        val validScore = repository.validateScore(
            score = 1,
            scoreLeft = 500,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Double,
            outMode = GameMode.Double,
        )

        validScore shouldBe false
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - invalid check in (master in)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(1)
        val validScore = repository.validateScore(
            score = 1,
            scoreLeft = 500,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Master,
            outMode = GameMode.Double,
        )

        validScore shouldBe false
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - valid score`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(51)
        val validScore = repository.validateScore(
            score = 51,
            scoreLeft = 352,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Double,
        )

        validScore shouldBe true
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - invalid score`() {
        val numberOfThrows = 1
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns emptySet()
        val validScore = repository.validateScore(
            score = 51,
            scoreLeft = 352,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Double,
        )

        validScore shouldBe false
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - valid check out (straight out)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(1)
        val validScore = repository.validateScore(
            score = 1,
            scoreLeft = 0,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Straight,
        )

        validScore shouldBe true
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - valid check out (double out)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(2)
        val validScore = repository.validateScore(
            score = 2,
            scoreLeft = 0,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Double,
        )

        validScore shouldBe true
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - invalid check out (double out)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(2)
        val validScore = repository.validateScore(
            score = 2,
            scoreLeft = 1,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Double,
        )

        validScore shouldBe false
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - valid check out (master out)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(2)
        val validScore = repository.validateScore(
            score = 2,
            scoreLeft = 0,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Master,
        )

        validScore shouldBe true
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test validating score - invalid check out (master out)`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleScoresFor(numberOfThrows) } returns setOf(2)
        val validScore = repository.validateScore(
            score = 2,
            scoreLeft = 1,
            startingScore = 501,
            numberOfThrows = numberOfThrows,
            inMode = GameMode.Straight,
            outMode = GameMode.Master,
        )

        validScore shouldBe false
        verify(exactly = 1) { dataSource.getPossibleScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test should ask for number of doubles (straight out) higher than 50`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        val shouldAskForNumberOfDoubles = repository.shouldAskForNumberOfDoubles(
            score = 2,
            scoreLeft = 51,
            numberOfThrows = numberOfThrows,
            outMode = GameMode.Straight,
        )

        shouldAskForNumberOfDoubles shouldBe false
        verify(inverse = true) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test should ask for number of doubles (straight out) lower than 50`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        val shouldAskForNumberOfDoubles = repository.shouldAskForNumberOfDoubles(
            score = 2,
            scoreLeft = 48,
            numberOfThrows = numberOfThrows,
            outMode = GameMode.Straight,
        )

        shouldAskForNumberOfDoubles shouldBe false
        verify(inverse = true) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test should ask for number of doubles (double out) higher than 50`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        val shouldAskForNumberOfDoubles = repository.shouldAskForNumberOfDoubles(
            score = 2,
            scoreLeft = 51,
            numberOfThrows = numberOfThrows,
            outMode = GameMode.Double,
        )

        shouldAskForNumberOfDoubles shouldBe false
        verify(inverse = true) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test should ask for number of doubles (double out) lower than 50`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) } returns setOf(2)
        val shouldAskForNumberOfDoubles = repository.shouldAskForNumberOfDoubles(
            score = 2,
            scoreLeft = 48,
            numberOfThrows = numberOfThrows,
            outMode = GameMode.Double,
        )

        shouldAskForNumberOfDoubles shouldBe true
        verify(exactly = 1) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test should ask for number of doubles (double out) lower than 50 invalid double-out score`() {
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) } returns emptySet()
        val shouldAskForNumberOfDoubles = repository.shouldAskForNumberOfDoubles(
            score = 2,
            scoreLeft = 48,
            numberOfThrows = numberOfThrows,
            outMode = GameMode.Double,
        )

        shouldAskForNumberOfDoubles shouldBe false
        verify(exactly = 1) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test calculate max number of doubles`() {
        val score = 25
        every { dataSource.isScorePossibleToDoubleOutWith(score, 1, 1) } returns false
        every { dataSource.isScorePossibleToDoubleOutWith(score, 2, 1) } returns true
        every { dataSource.isScorePossibleToDoubleOutWith(score, 2, 2) } returns false
        every { dataSource.isScorePossibleToDoubleOutWith(score, 3, 1) } returns true
        every { dataSource.isScorePossibleToDoubleOutWith(score, 3, 2) } returns true
        every { dataSource.isScorePossibleToDoubleOutWith(score, 3, 3) } returns false
        val maxNumberOfDoubles = repository.calculateMaxNumberOfDoubles(score)

        maxNumberOfDoubles shouldContainExactly mapOf(
            1 to 1,
            2 to 1,
            3 to 2,
        )
        verify { dataSource.isScorePossibleToDoubleOutWith(score, range(1, 3), range(1, 3)) }
    }

    @Test
    fun `Test calculate min number of throws (straight out)`() {
        val score = 25
        every { dataSource.getPossibleOutScoresFor(1) } returns emptySet()
        every { dataSource.getPossibleOutScoresFor(2) } returns emptySet()
        every { dataSource.getPossibleOutScoresFor(3) } returns setOf(score)
        val minNumberOfThrows = repository.calculateMinNumberOfThrows(score, GameMode.Straight)

        minNumberOfThrows shouldBeExactly 3
        verify(atMost = 3) { dataSource.getPossibleOutScoresFor(range(1, 3)) }
    }

    @Test
    fun `Test calculate min number of throws (double out)`() {
        val score = 25
        every { dataSource.getPossibleDoubleOutScoresFor(1) } returns emptySet()
        every { dataSource.getPossibleDoubleOutScoresFor(2) } returns setOf(score)
        every { dataSource.getPossibleDoubleOutScoresFor(3) } returns emptySet()
        val minNumberOfThrows = repository.calculateMinNumberOfThrows(score, GameMode.Double)

        minNumberOfThrows shouldBeExactly 2
        verify(atMost = 3) { dataSource.getPossibleDoubleOutScoresFor(range(1, 3)) }
    }

    @Test
    fun `Test calculate min number of throws (master out)`() {
        val score = 25
        every { dataSource.getPossibleMasterOutScoresFor(1) } returns setOf(score)
        every { dataSource.getPossibleMasterOutScoresFor(2) } returns emptySet()
        every { dataSource.getPossibleMasterOutScoresFor(3) } returns emptySet()
        val minNumberOfThrows = repository.calculateMinNumberOfThrows(score, GameMode.Master)

        minNumberOfThrows shouldBeExactly 1
        verify(atMost = 3) { dataSource.getPossibleMasterOutScoresFor(range(1, 3)) }
    }

    @Test
    fun `Test is checkout possible (straight out) - valid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleOutScoresFor(numberOfThrows) } returns setOf(score)
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Straight,
        )

        isCheckoutPossible shouldBe true
        verify(exactly = 1) { dataSource.getPossibleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test is checkout possible (straight out) - invalid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleOutScoresFor(numberOfThrows) } returns emptySet()
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Straight,
        )

        isCheckoutPossible shouldBe false
        verify(exactly = 1) { dataSource.getPossibleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test is checkout possible (double out) - valid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) } returns setOf(score)
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Double,
        )

        isCheckoutPossible shouldBe true
        verify(exactly = 1) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test is checkout possible (double out) - invalid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) } returns emptySet()
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Double,
        )

        isCheckoutPossible shouldBe false
        verify(exactly = 1) { dataSource.getPossibleDoubleOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test is checkout possible (master out) - valid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleMasterOutScoresFor(numberOfThrows) } returns setOf(score)
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Master,
        )

        isCheckoutPossible shouldBe true
        verify(exactly = 1) { dataSource.getPossibleMasterOutScoresFor(numberOfThrows) }
    }

    @Test
    fun `Test is checkout possible (master out) - invalid`() {
        val score = 25
        val numberOfThrows = DEFAULT_NUMBER_OF_THROWS
        every { dataSource.getPossibleMasterOutScoresFor(numberOfThrows) } returns emptySet()
        val isCheckoutPossible = repository.isCheckoutPossible(
            score,
            numberOfThrows,
            GameMode.Master,
        )

        isCheckoutPossible shouldBe false
        verify(exactly = 1) { dataSource.getPossibleMasterOutScoresFor(numberOfThrows) }
    }
}
