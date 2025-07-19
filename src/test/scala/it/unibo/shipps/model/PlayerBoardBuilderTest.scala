package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*

import it.unibo.shipps.exceptions.PositionOccupiedException
import it.unibo.shipps.model.PlayerBoardBuilder.*
import it.unibo.shipps.model.ShipType.*

import matchers.*
import scala.language.postfixOps

/** Test suite for the [[PlayerBoardBuilder]]. */
class PlayerBoardBuilderTest extends AnyFlatSpec with should.Matchers:

  "The DSL of PlayerBoardBuilder" should "support placement of a single horizontal ship" in:
    PlayerBoardBuilder
      .board(
        place a Carrier at A(1) horizontal
      )
      .getShips.head.getPositions shouldEqual Set(
      Position(0, 0),
      Position(1, 0),
      Position(2, 0),
      Position(3, 0),
      Position(4, 0)
    )

  it should "support placement of a vertical ship" in:
    PlayerBoardBuilder
      .board(
        place a Destroyer at B(2) vertical
      )
      .getShips.head.getPositions shouldEqual Set(
      Position(1, 1),
      Position(1, 2),
      Position(1, 3),
      Position(1, 4)
    )

  it should "allow placing multiple non-overlapping ships" in:
    PlayerBoardBuilder
      .board(
        place a Carrier at A(1) horizontal,
        place a Submarine at A(2) horizontal,
        place a Frigate at J(10) vertical
      )
      .getShips.size shouldEqual 3

  it should "throw PositionOccupiedException if ships overlap" in:
    a[PositionOccupiedException] should be thrownBy:
      PlayerBoardBuilder.board(
        place a Carrier at A(1) horizontal,
        place a Submarine at A(1) horizontal
      )
