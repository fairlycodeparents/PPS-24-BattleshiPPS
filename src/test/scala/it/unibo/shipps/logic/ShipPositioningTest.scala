package it.unibo.shipps.logic

import it.unibo.shipps.model
import it.unibo.shipps.model.Orientation.{Horizontal, Vertical}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.shipps.model.{ConcretePosition, PlayerBoard, Position, Ship, ShipImpl, ShipPositioning, ShipType}
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable

class ShipPositioningTest extends AnyFunSuite with Matchers {

  /** The ship positioning logic to be tested */
  val shipPositioning: ShipPositioning = new ShipPositioning {}

  /** Creates a list of ships of the specified type and count.
    * @param count the number of ships to create
    * @param shipType the type of ship to create
    * @return a list of ships
    */
  def createMultipleShips(count: Int, shipType: Ship): List[Ship] =
    List.fill(count)(shipType)

  // Test cases
  test("getShipAt should successfully return a ship at a given position") {
    val ship     = ShipImpl(ShipType.Frigate, ConcretePosition(1, 1), Vertical)
    val board    = PlayerBoard(Set(ship))
    val position = ConcretePosition(1, 1)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isRight, "Expected to find a ship at the specified position")
    result.value shouldBe ship
  }

  test("getShipAt should return an error when no ship is found at the position") {
    val board    = PlayerBoard()
    val position = ConcretePosition(2, 2)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isLeft, "Expected an error when no ship is found at the position")
    result.left.value should include("No ship found at the selected position.")
  }

  test("placeShip should successfully place a ship on empty board") {
    val ship     = ShipImpl(ShipType.Frigate, ConcretePosition(1, 1), Vertical)
    val board    = PlayerBoard(Set(ship))
    val position = ConcretePosition(2, 3)

    val result = shipPositioning.placeShip(board, ship, position)

    assert(result.isRight, "Expected successful placement of ship on empty board")
    val updatedBoard = result.value
    updatedBoard.getShips should have size 1
    updatedBoard.getShips.head.getAnchor shouldBe position
  }

  test("placeShip should fail when ship overlaps with existing ship") {
    val existingShip        = ShipImpl(ShipType.Frigate, ConcretePosition(1, 1), Vertical)
    val newShip             = ShipImpl(ShipType.Submarine, ConcretePosition(1, 1), Vertical)
    val board               = PlayerBoard(Set(existingShip, newShip))
    val overlappingPosition = ConcretePosition(1, 1)

    val result = shipPositioning.placeShip(board, newShip, overlappingPosition)

    assert(result.isLeft, "Expected an error when placing a ship that overlaps with an existing ship")
    result.left.value should include("overlap")
  }

  test("placeShip should fail when ship is out of bounds") {
    val ship  = ShipImpl(ShipType.Carrier, ConcretePosition(1, 1), Horizontal)
    val board = PlayerBoard(Set(ship))

    val result = shipPositioning.placeShip(board, ship, ConcretePosition(8, 8))

    assert(result.isLeft, "Expected an error when placing a ship out of bounds")
    result.left.value should include("out of bounds")
  }

  test("randomPositioning should return an error if unable to place all ships") {
    val board = PlayerBoard()
    val ships =
      createMultipleShips(21, ShipImpl(ShipType.Carrier, ConcretePosition(1, 1), Vertical))

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isLeft, "Expected an error when unable to randomly position all ships")
    result.left.value should include("Failed to place all ships after maximum attempts.")
  }

  test("randomPositioning should place all ships successfully") {
    val board = PlayerBoard()
    val ships = List(
      ShipImpl(ShipType.Frigate, ConcretePosition(1, 1), Vertical),
      ShipImpl(ShipType.Submarine, ConcretePosition(1, 1), Vertical)
    )

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isRight, "Expected successful random positioning of ships")
    result.value.getShips should have size 2
  }
}
