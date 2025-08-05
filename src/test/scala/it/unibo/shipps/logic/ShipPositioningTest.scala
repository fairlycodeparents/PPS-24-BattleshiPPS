package it.unibo.shipps.logic

import it.unibo.shipps.model
import it.unibo.shipps.model.Orientation.{Horizontal, Vertical}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.shipps.model.{Ship, ShipImpl, ShipPositioning, ShipPositioningImpl, ShipType}
import it.unibo.shipps.model.board.PlayerBoardBuilder.*
import it.unibo.shipps.model.board.PlayerBoardBuilder.place.a
import it.unibo.shipps.model.ShipType.{Carrier, Frigate, Submarine}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable

import scala.language.postfixOps

class ShipPositioningTest extends AnyFunSuite with Matchers {

  /** The ship positioning logic to be tested */
  val shipPositioning: ShipPositioning = ShipPositioningImpl

  /** Creates a list of ships of the specified type and count.
    *
    * @param count    the number of ships to create
    * @param shipType the type of ship to create
    * @return a list of ships
    */
  def createMultipleShips(count: Int, shipType: Ship): List[Ship] =
    List.fill(count)(shipType)

  // Test cases
  test("getShipAt should successfully return a ship at a given position") {
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val position = A(1)

    val result = shipPositioning.getShipAt(testBoard, position)

    assert(result.isRight, "Expected to find a ship at the specified position")
    result.value.anchor shouldBe A(1)
  }

  test("getShipAt should return an error when no ship is found at the position") {
    val testBoard = board()
    val position  = B(2)

    val result = shipPositioning.getShipAt(testBoard, position)

    assert(result.isLeft, "Expected an error when no ship is found at the position")
    result.left.value should include("No ship found at the selected position.")
  }

  test("placeShip should successfully place a ship on empty board") {
    val ship  = Frigate.at(A(1), Vertical)
    val board = PlayerBoard()

    val result = shipPositioning.placeShip(board, ship)

    assert(result.isRight, "Expected successful placement of ship on empty board")
    val updatedBoard = result.value
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.anchor shouldBe A(1)
  }

  test("placeShip should fail when ship overlaps with existing ship") {
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val newShip = Submarine.at(A(1), Vertical)

    val result = shipPositioning.placeShip(testBoard, newShip)

    assert(result.isLeft, "Expected an error when placing a ship that overlaps with an existing ship")
    result.left.value should include("overlap")
  }

  test("placeShip should fail when ship is out of bounds") {
    val ship  = Carrier.at(H(8), Horizontal)
    val board = PlayerBoard()

    val result = shipPositioning.placeShip(board, ship)

    assert(result.isLeft, "Expected an error when placing a ship out of bounds")
    result.left.value should include("out of bounds")
  }

  test("moveShip should successfully move a ship to a new position") {
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val ship        = testBoard.ships.head
    val newPosition = C(1)

    val result = shipPositioning.moveShip(testBoard, ship, newPosition)

    assert(result.isRight, "Expected successful movement of ship")
    val updatedBoard = result.value
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.anchor shouldBe newPosition
  }

  test("moveShip should fail when ship overlaps with another ship") {
    val testBoard = board(
      place a Frigate at A(1) vertical,
      place a Submarine at B(1) vertical
    )
    val overlappingShip = testBoard.ships.find(_.anchor == B(1)).get

    val result = shipPositioning.moveShip(testBoard, overlappingShip, A(1))

    assert(result.isLeft, "Expected an error when moving a ship that overlaps with another ship")
    result.left.value should include("overlaps with another ship")
  }

  test("moveShip should fail when ship is out of bounds") {
    val testBoard = board(
      place a Carrier at A(1) horizontal
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.moveShip(testBoard, ship, H(8))

    assert(result.isLeft, "Expected an error when placing a ship out of bounds")
    result.left.value should include("out of bounds")
  }

  test("rotateShip should successfully rotate a ship") {
    val testBoard = board(
      place a Frigate at A(1) vertical
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.rotateShip(testBoard, ship)

    assert(result.isRight, "Expected successful rotation of ship")
    val updatedBoard = result.value
    updatedBoard.ships should have size 1
    updatedBoard.ships.head.shape.getOrientation shouldBe Horizontal
  }

  test("rotateShip should fail when rotated ship overlaps with another ship") {
    val testBoard = board(
      place a Submarine at A(2) vertical,
      place a Frigate at A(1) horizontal
    )
    val shipToRotate = testBoard.ships.find(_.shape.getOrientation == Horizontal).get

    val result = shipPositioning.rotateShip(testBoard, shipToRotate)

    assert(result.isLeft, "Expected an error when rotating a ship that overlaps with another ship")
    result.left.value should include("overlaps with another ship")
  }

  test("rotateShip should fail when rotated ship is out of bounds") {
    val testBoard = board(
      place a Carrier at A(8) horizontal
    )
    val ship = testBoard.ships.head

    val result = shipPositioning.rotateShip(testBoard, ship)

    assert(result.isLeft, "Expected an error when rotating a ship out of bounds")
    result.left.value should include("out of bounds")
  }

  test("randomPositioning should return an error if unable to place all ships") {
    val board = PlayerBoard()
    val ships = createMultipleShips(21, Carrier.at(A(1), Vertical))

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isLeft, "Expected an error when unable to randomly position all ships")
    result.left.value should include("Failed to place all ships after maximum attempts.")
  }

  test("randomPositioning should place all ships successfully") {
    val board = PlayerBoard()
    val ships = List(
      Frigate.at(A(1), Vertical),
      Submarine.at(A(1), Vertical)
    )

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isRight, "Expected successful random positioning of ships")
    result.value.ships should have size 2
  }
}
