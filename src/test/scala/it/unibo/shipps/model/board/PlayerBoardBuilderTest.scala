package it.unibo.shipps.model.board

import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.model.board.BoardCoordinates.*
import it.unibo.shipps.model.board.PlayerBoardBuilder.*
import it.unibo.shipps.model.board.ShipPlacementDSL.place
import org.scalatest.*
import org.scalatest.flatspec.*
import org.scalatest.matchers.*

import scala.language.postfixOps

object TestHelpers:
  /** Extension method to extract all positions occupied by ships on a player board. */
  extension (b: PlayerBoard) def positions: Set[Position] = b.ships.flatMap(_.positions)

/** Test suite for the [[PlayerBoardBuilder]]. */
class PlayerBoardBuilderTest extends AnyFlatSpec with should.Matchers:
  import TestHelpers.*

  "The DSL of PlayerBoardBuilder" should "support placement of a single horizontal ship" in:
    board(place a Carrier at A(1) horizontal).getOrElse(fail()).positions shouldEqual Position(0 to 4, 0)

  it should "support placement of a vertical ship" in:
    board(place a Destroyer at B(2) vertical).getOrElse(fail()).positions shouldEqual Position(1, 1 to 4)

  it should "allow placing multiple non-overlapping ships" in:
    val multiShipBoard = board(
      place a Carrier at A(1) horizontal,
      place a Submarine at E(2) horizontal,
      place a Frigate at J(4) vertical
    ).getOrElse(fail())
    multiShipBoard.ships.size shouldEqual 3

  "Placing ships" should "handle a placement at the board's edge (top-left)" in:
    val boardWithFrigate = board(place a Frigate at A(1) horizontal).getOrElse(fail())
    boardWithFrigate.ships.head.positions shouldEqual Position(0 to 1, 0)

  it should "handle a placement at the board's edge (bottom-right)" in:
    board(place a Destroyer at J(7) vertical).getOrElse(fail()).positions shouldEqual Position(9, 6 to 9)

  it should "return Left if ships overlap" in:
    val result = board(
      place a Carrier at D(1) horizontal,
      place a Submarine at D(1) horizontal
    )
    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("overlaps")

  it should "return Left if a ship goes out of bounds" in:
    val result = board(
      place a Carrier at J(1) horizontal
    )
    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("out of bounds")

  it should "return Left with an invalid coordinate" in:
    val result = board(
      place a Carrier at A(11) horizontal
    )
    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("out of bounds")

  it should "throw RuntimeException if an unexisting position is used" in:
    a[RuntimeException] should be thrownBy column('Z')(1)