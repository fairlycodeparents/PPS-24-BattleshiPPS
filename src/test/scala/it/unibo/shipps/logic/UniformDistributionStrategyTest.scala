package it.unibo.shipps.logic

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.UniformDistributionStrategy
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import matchers.*

import scala.language.postfixOps

/** Test suite for the [[UniformDistributionStrategy]] class. */
class UniformDistributionStrategyTest extends AnyFlatSpec with should.Matchers:

  val strategy: UniformDistributionStrategy = new UniformDistributionStrategy()

  "The strategy" should "return an error when a position is provided for a bot attack" in:
    val positionToAttack             = Some(Position(5, 5))
    val boardWithNoHits: PlayerBoard = PlayerBoard()
    val (returnedBoard, result)      = strategy.execute(boardWithNoHits, positionToAttack)

    returnedBoard shouldBe boardWithNoHits
    result.isLeft shouldBe true

  it should "return an error when there are no positions left to attack" in:
    val allPositions: Set[Position] =
      (for
        x <- 0 until PlayerBoard.size
        y <- 0 until PlayerBoard.size
      yield Position(x, y)).toSet
    val boardWithAllPositionsHit: PlayerBoard = PlayerBoard(hits = allPositions)
    val (returnedBoard, result)               = strategy.execute(boardWithAllPositionsHit, None)

    returnedBoard shouldBe boardWithAllPositionsHit
    result.isLeft shouldBe true

  it should "choose the farthest corner when only the opposite corner is hit" in:
    val hitPosition             = Position(0, 0)
    val boardWithOneHit         = PlayerBoard(hits = Set(hitPosition))
    val expectedPosition        = Position(PlayerBoard.size - 1, PlayerBoard.size - 1)
    val (returnedBoard, result) = strategy.execute(boardWithOneHit, None)
    val newHit                  = returnedBoard.hits.diff(boardWithOneHit.hits).head

    result.isRight shouldBe true
    newHit shouldBe expectedPosition

  it should "choose a position on the anti-diagonal or center when both main diagonal corners are hit" in:
    val cornerPositions       = Set(Position(0, 0), Position(PlayerBoard.size - 1, PlayerBoard.size - 1))
    val boardWithTwoHits      = PlayerBoard(hits = cornerPositions)
    val expectedBestPositions = for i <- 0 until PlayerBoard.size yield Position(PlayerBoard.size - 1 - i, i)
    val (newBoard, result)    = strategy.execute(boardWithTwoHits, None)
    val newHit = newBoard
      .hits
      .diff(boardWithTwoHits.hits)
      .head

    result.isRight shouldBe true
    expectedBestPositions should contain(newHit)
