package com.pointlessapps.dartify.local.datasource.game.x01.turn

import com.pointlessapps.dartify.datasource.game.x01.move.model.InputScore
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.shouldBeSameInstanceAs

internal class PlayerScoreHandlerTest : AnnotationSpec() {

    @Test
    fun `Test first leg basic inputs`() {
        val throw1 = InputScore.Turn(100)
        val throw2 = InputScore.Turn(20)

        val handler = PlayerScoreHandler(501)
        handler.addInput(throw1, 3, 0)
        handler.average shouldBeExactly 100f
        handler.addInput(throw2, 3, 0)
        handler.average shouldBeExactly 60f
        handler.scoreLeft shouldBeExactly 501 - 120
        handler.max shouldBeExactly 100
        handler.numberOfDarts shouldBeExactly 6
        handler.lastScore should {
            it shouldNot beNull()
            it!! shouldBeExactly 20
        }
        handler.doublePercentage shouldBeExactly 0f
        handler.hasWonPreviousLeg() shouldBe false
        handler.hasNoInputs() shouldBe false
        handler.popInput() shouldBe throw2
        handler.lastScore should {
            it shouldNot beNull()
            it!! shouldBeExactly 100
        }
        handler.numberOfDarts shouldBeExactly 3
        handler.popInput() shouldBe throw1
        handler.numberOfDarts shouldBeExactly 0
        handler.lastScore should beNull()
        handler.popInput() should beNull()
        handler.lastScore should beNull()
    }

    @Test
    fun `Test finishing a leg`() {
        val throw1 = InputScore.Turn(26)
        val throw2 = InputScore.Turn(69)
        val throw3 = InputScore.Turn(6)
        val throw4 = InputScore.Turn(99)
        val throw5 = InputScore.Turn(2)

        val handler = PlayerScoreHandler(101)
        handler.addInput(throw1, 3, 0)
        handler.average shouldBeExactly 26f
        handler.scoreLeft shouldBeExactly 75
        handler.addInput(throw2, 3, 3)
        handler.average shouldBeExactly 47.5f
        handler.scoreLeft shouldBeExactly 6
        handler.max shouldBeExactly 69
        handler.addInput(throw3, 2, 2)
        handler.scoreLeft shouldBeExactly 0
        handler.numberOfDarts shouldBeExactly 8
        handler.markLegAsFinished(true)
        handler.doublePercentage shouldBeExactly 0.2f
        handler.wonLegs shouldBeExactly 1
        handler.wonSets shouldBeExactly 0
        handler.lastScore should beNull()
        handler.scoreLeft shouldBeExactly 101
        handler.numberOfDarts shouldBeExactly 0
        handler.average shouldBeExactly 37.875f
        handler.addInput(throw4, 3, 0)
        handler.addInput(throw5, 3, 3)
        handler.markLegAsFinished(true)
        handler.doublePercentage shouldBeExactly 0.25f
        handler.scoreLeft shouldBeExactly 101
        handler.wonLegs shouldBeExactly 2
        handler.wonSets shouldBeExactly 0
        handler.popInput() shouldBe throw5
        handler.popInput() shouldBe throw4
        handler.popInput() shouldBe throw3
        handler.wonLegs shouldBeExactly 0
        handler.scoreLeft shouldBeExactly 6
        handler.markLegAsFinished(false)
        handler.doublePercentage shouldBeExactly 0f
        handler.average shouldBeExactly 47.5f
    }

    @Test
    fun `Test finishing a set`() {
        val throw1 = InputScore.Turn(75)
        val throw2 = InputScore.Turn(101)
        val throw3 = InputScore.Turn(101)
        val throw4 = InputScore.Turn(10)

        val handler = PlayerScoreHandler(101)
        handler.addInput(throw1, 3, 1)
        handler.markLegAsFinished(false)
        handler.scoreLeft shouldBeExactly 101
        handler.average shouldBeExactly 75f
        handler.lastScore should beNull()
        handler.max shouldBeExactly 75
        handler.addInput(throw2, 3, 1)
        handler.markSetAsFinished(true)
        handler.max shouldBeExactly 101
        handler.doublePercentage shouldBeExactly 0.5f
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.addInput(throw3, 2, 1)
        handler.markLegAsFinished(true)
        handler.wonLegs shouldBeExactly 1
        handler.wonSets shouldBeExactly 1
        handler.doublePercentage shouldBeExactly 2 / 3f
        handler.average shouldBeExactly 103.875f
        handler.addInput(throw4, 3, 0)
        handler.markSetAsFinished(false)
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.popInput() shouldBe throw4
        handler.popInput() shouldBe throw3
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.popInput() shouldBe throw2
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 0
        handler.max shouldBeExactly 75
    }
}
