package it.unibo.shipps.logic

import it.unibo.shipps.model
import it.unibo.shipps.model.ShipOrientation.{Horizontal, Vertical}
import org.scalatest.*
import flatspec.*
import matchers.*
import it.unibo.shipps.model.{Ship, ShipPositioning, ShipPositioningImpl, ShipType}
import it.unibo.shipps.model.board.PlayerBoardBuilder.*
import it.unibo.shipps.model.ShipType.{Carrier, Frigate, Submarine}
import it.unibo.shipps.model.board.ShipPlacementDSL.place
import it.unibo.shipps.model.board.BoardCoordinates._
import it.unibo.shipps.model.board.{PlayerBoard, Position}

import scala.language.postfixOps

/** Test suite for the ShipPositioning logic. */
class ShipPositioningTest extends AnyFlatSpec with should.Matchers:

  /** The ship positioning logic to be tested */
  val shipPositioning: ShipPositioning = ShipPositioningImpl

  /** Creates a list of ships of the specified type and count.
    * @param count    the number of ships to create
    * @param shipType the type of ship to create
    * @return a list of ships
    */
  def createMultipleShips(count: Int, shipType: Ship): List[Ship] =
    List.fill(count)(shipType)

  "ShipPositioning" should "successfully return a ship at a given position" in:
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val position = A(1)

    val result = shipPositioning.getShipAt(testBoard, position)

    result.isRight shouldBe true
    result.getOrElse(fail()).anchor shouldBe A(1)

  it should "return an error when no ship is found at the position" in:
    val testBoard = board()
    val position  = B(2)

    val result = shipPositioning.getShipAt(testBoard, position)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("No ship found at the selected position.")

  it should "successfully place a ship on empty board" in:
    val ship  = Frigate.at(A(1), Vertical)
    val board = PlayerBoard()

    val result = shipPositioning.placeShip(board, ship)

    result.isRight shouldBe true
    val updatedBoard = result.getOrElse(fail())
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.anchor shouldBe A(1)

  it should "fail when ship overlaps with existing ship" in:
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val newShip = Submarine.at(A(1), Vertical)

    val result = shipPositioning.placeShip(testBoard, newShip)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("overlap")

  it should "fail when ship is out of bounds" in:
    val ship  = Carrier.at(H(8), Horizontal)
    val board = PlayerBoard()

    val result = shipPositioning.placeShip(board, ship)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("out of bounds")

  it should "successfully move a ship to a new position" in:
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val ship        = testBoard.ships.head
    val newPosition = C(1)

    val result = shipPositioning.moveShip(testBoard, ship, newPosition)

    result.isRight shouldBe true
    val updatedBoard = result.getOrElse(fail())
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.anchor shouldBe newPosition

  it should "fail when moving ship overlaps with another ship" in:
    val testBoard = board(
      place a Frigate at A(1) vertical,
      place a Submarine at B(1) vertical
    )
    val overlappingShip = testBoard.ships.find(_.anchor == B(1)).getOrElse(fail())

    val result = shipPositioning.moveShip(testBoard, overlappingShip, A(1))

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("overlaps with another ship")

  it should "fail when moved ship is out of bounds" in:
    val testBoard = board(
      place a Carrier at A(1) horizontal
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.moveShip(testBoard, ship, H(8))

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("out of bounds")

  it should "successfully rotate a ship" in:
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.rotateShip(testBoard, ship)

    result.isRight shouldBe true
    val updatedBoard = result.getOrElse(fail())
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.orientation shouldBe Horizontal

  it should "fail when rotated ship overlaps with another ship" in:
    val testBoard = board(
      place a Submarine at A(2) vertical,
      place a Frigate at A(1) horizontal
    )
    val shipToRotate = testBoard.ships
      .find(_.orientation == Horizontal)
      .getOrElse(fail())

    val result = shipPositioning.rotateShip(testBoard, shipToRotate)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("overlaps with another ship")

  it should "fail when rotated ship is out of bounds" in:
    val testBoard = board(
      place a Carrier at A(8) horizontal
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.rotateShip(testBoard, ship)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("out of bounds")

  it should "return an error if unable to place all ships randomly" in:
    val board = PlayerBoard()
    val ships = createMultipleShips(21, Carrier.at(A(1), Vertical))

    val result = shipPositioning.randomPositioning(board, ships)

    result.isLeft shouldBe true
    result.left.getOrElse(fail()) should include("Failed to place all ships after maximum attempts.")

  it should "place all ships successfully with random positioning" in:
    val board = PlayerBoard()
    val ships = List(
      Frigate.at(A(1), Vertical),
      Submarine.at(A(1), Vertical)
    )

    val result = shipPositioning.randomPositioning(board, ships)

    result.isRight shouldBe true
    result.getOrElse(fail()).ships should have size 2
