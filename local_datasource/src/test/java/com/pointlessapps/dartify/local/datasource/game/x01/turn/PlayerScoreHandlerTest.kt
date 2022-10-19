package com.pointlessapps.dartify.local.datasource.game.x01.turn

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.floats.shouldBeExactly
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot

internal class PlayerScoreHandlerTest : AnnotationSpec() {

    @Test
    fun `Test first leg basic inputs`() {
        val handler = PlayerScoreHandler(501)
        handler.addInput(100, 3, 0)
        handler.average shouldBeExactly 100f
        handler.addInput(20, 3, 0)
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
        handler.popInput() shouldBeExactly 20
        handler.lastScore should {
            it shouldNot beNull()
            it!! shouldBeExactly 100
        }
        handler.numberOfDarts shouldBeExactly 3
        handler.popInput() shouldBeExactly 100
        handler.numberOfDarts shouldBeExactly 0
        handler.lastScore should beNull()
        handler.popInput() shouldBeExactly 0
        handler.lastScore should beNull()
    }

    @Test
    fun `Test finishing a leg`() {
        val handler = PlayerScoreHandler(101)
        handler.addInput(26, 3, 0)
        handler.average shouldBeExactly 26f
        handler.scoreLeft shouldBeExactly 75
        handler.addInput(69, 3, 3)
        handler.average shouldBeExactly 47.5f
        handler.scoreLeft shouldBeExactly 6
        handler.max shouldBeExactly 69
        handler.addInput(6, 2, 2)
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
        handler.addInput(99, 3, 0)
        handler.addInput(2, 3, 3)
        handler.markLegAsFinished(true)
        handler.doublePercentage shouldBeExactly 0.25f
        handler.scoreLeft shouldBeExactly 101
        handler.wonLegs shouldBeExactly 2
        handler.wonSets shouldBeExactly 0
        handler.popInput() shouldBeExactly 2
        handler.popInput() shouldBeExactly 99
        handler.popInput() shouldBeExactly 6
        handler.wonLegs shouldBeExactly 0
        handler.scoreLeft shouldBeExactly 6
        handler.markLegAsFinished(false)
        handler.doublePercentage shouldBeExactly 0f
        handler.average shouldBeExactly 47.5f
    }

    @Test
    fun `Test finishing a set`() {
        val handler = PlayerScoreHandler(101)
        handler.addInput(75, 3, 1)
        handler.markLegAsFinished(false)
        handler.scoreLeft shouldBeExactly 101
        handler.average shouldBeExactly 75f
        handler.lastScore should beNull()
        handler.max shouldBeExactly 75
        handler.addInput(101, 3, 1)
        handler.markSetAsFinished(true)
        handler.max shouldBeExactly 101
        handler.doublePercentage shouldBeExactly 0.5f
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.addInput(101, 2, 1)
        handler.markLegAsFinished(true)
        handler.wonLegs shouldBeExactly 1
        handler.wonSets shouldBeExactly 1
        handler.doublePercentage shouldBeExactly 2 / 3f
        handler.average shouldBeExactly 103.875f
        handler.addInput(10, 3, 0)
        handler.markSetAsFinished(false)
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.popInput() shouldBeExactly 10
        handler.popInput() shouldBeExactly 101
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 1
        handler.popInput() shouldBeExactly 101
        handler.wonLegs shouldBeExactly 0
        handler.wonSets shouldBeExactly 0
        handler.max shouldBeExactly 75
    }
}
