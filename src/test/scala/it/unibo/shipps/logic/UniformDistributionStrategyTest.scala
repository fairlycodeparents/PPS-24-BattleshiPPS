package it.unibo.shipps.logic

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.{MaxMinPositionWeighting, UniformDistributionStrategy}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.board.BoardCoordinates.*
import matchers.*

import scala.language.postfixOps

/** Test suite for the [[UniformDistributionStrategy]] class. */
class UniformDistributionStrategyTest extends AnyFlatSpec with should.Matchers:

  val strategy: UniformDistributionStrategy = new UniformDistributionStrategy(MaxMinPositionWeighting())

  "The strategy" should "return an error when a position is provided for a bot attack" in:
    val boardWithNoHits: PlayerBoard = PlayerBoard()
    val (returnedBoard, result)      = strategy.execute(boardWithNoHits, Some(F(6)))

    returnedBoard shouldBe boardWithNoHits
    result.isLeft shouldBe true

  it should "return an error when there are no positions left to attack" in:
    val allPositions =
      for
        x <- 0 until PlayerBoard.size
        y <- 0 until PlayerBoard.size
      yield Position(x, y)
    val boardWithAllPositionsHit: PlayerBoard = PlayerBoard(hits = allPositions.toSet)
    val (returnedBoard, result)               = strategy.execute(boardWithAllPositionsHit, None)

    returnedBoard shouldBe boardWithAllPositionsHit
    result.isLeft shouldBe true

  it should "choose the farthest corner when only the opposite corner is hit" in:
    val boardWithOneHit         = PlayerBoard(hits = Set(A(1)))
    val expectedPosition        = J(10)
    val (returnedBoard, result) = strategy.execute(boardWithOneHit, None)
    val newHit                  = returnedBoard.hits.diff(boardWithOneHit.hits).head

    result.isRight shouldBe true
    newHit shouldBe expectedPosition

  it should "choose a position on the anti-diagonal or center when both main diagonal corners are hit" in:
    val cornerPositions       = Set(A(1), J(10))
    val boardWithTwoHits      = PlayerBoard(hits = cornerPositions)
    val expectedBestPositions = for i <- 0 until PlayerBoard.size yield Position(PlayerBoard.size - 1 - i, i)
    val (newBoard, result)    = strategy.execute(boardWithTwoHits, None)
    val newHit = newBoard
      .hits
      .diff(boardWithTwoHits.hits)
      .head

    result.isRight shouldBe true
    expectedBestPositions should contain(newHit)
