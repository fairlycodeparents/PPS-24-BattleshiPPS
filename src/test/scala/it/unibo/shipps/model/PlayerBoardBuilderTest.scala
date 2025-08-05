package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.board.PlayerBoardBuilder.*
import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.model.board.BoardCoordinates.*
import it.unibo.shipps.model.board.ShipPlacementDSL.place
import it.unibo.shipps.model.board.{PlayerBoardBuilder, Position}
import matchers.*

import scala.language.postfixOps

/** Test suite for the [[PlayerBoardBuilder]]. */
class PlayerBoardBuilderTest extends AnyFlatSpec with should.Matchers:

  "The DSL of PlayerBoardBuilder" should "support placement of a single horizontal ship" in:
    board(place a Carrier at A(1) horizontal)
      .ships.head.positions shouldEqual Set(
      Position(0, 0),
      Position(1, 0),
      Position(2, 0),
      Position(3, 0),
      Position(4, 0)
    )

  it should "support placement of a vertical ship" in:
    val boardWithDestroyer = board(place a Destroyer at B(2) vertical)
    boardWithDestroyer.ships.head.positions shouldEqual Set(
      Position(1, 1),
      Position(1, 2),
      Position(1, 3),
      Position(1, 4)
    )

  it should "allow placing multiple non-overlapping ships" in:
    val multiShipBoard = board(
      place a Carrier at A(1) horizontal,
      place a Submarine at A(2) horizontal,
      place a Frigate at J(4) vertical
    )
    multiShipBoard.ships.size shouldEqual 3

  "Placing ships" should "handle a placement at the board's edge (top-left)" in:
    val boardWithFrigate = board(place a Frigate at A(1) horizontal)
    boardWithFrigate.ships.head.positions shouldEqual Set(Position(0, 0), Position(1, 0))

  it should "handle a placement at the board's edge (bottom-right)" in:
    val boardWithDestroyer = board(place a Destroyer at J(7) vertical)
    boardWithDestroyer.ships.head.positions shouldEqual Set(
      Position(9, 6),
      Position(9, 7),
      Position(9, 8),
      Position(9, 9)
    )

  it should "throw RuntimeException if ships overlap" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at A(1) horizontal,
      place a Submarine at A(1) horizontal
    )

  it should "throw RuntimeException if a ship goes out of bounds" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at J(1) horizontal
    )

  it should "throw RuntimeException with an invalid coordinate" in:
    a[RuntimeException] should be thrownBy board(
      place a Carrier at A(11) horizontal
    )
