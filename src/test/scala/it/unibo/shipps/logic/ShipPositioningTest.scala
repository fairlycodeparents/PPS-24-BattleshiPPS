package it.unibo.shipps.logic

import it.unibo.shipps.model
import it.unibo.shipps.model.Orientation.Vertical
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.shipps.model.{
  ConcretePosition,
  DefaultShipFactory,
  PlayerBoard,
  Position,
  Ship,
  ShipPositioning,
  ShipType
}
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable

class ShipPositioningTest extends AnyFunSuite with Matchers {

  case class MockPlayerBoard(ships: Set[Ship] = Set.empty) extends PlayerBoard:

    def getShips: Set[Ship] = ships

    def addShip(ship: Ship): PlayerBoard =
      this.copy(ships = ships + ship)

    def removeShip(ship: Ship): PlayerBoard =
      this.copy(ships = ships.filterNot(_ == ship))

    def isAnyPositionOccupied(positions: Set[Position]): Boolean =
      val allOccupiedPositions = ships.flatMap(_.getPositions)
      positions.exists(allOccupiedPositions.contains)

    override val width: Int  = 10
    override val height: Int = 10

  def createMultipleShips(count: Int, shipType: Ship): List[Ship] =
    List.fill(count)(shipType)

  // Test cases
  val shipPositioning: ShipPositioning = new ShipPositioning {}
  test("getShipAt should successfully return a ship at a given position") {
    val ship     = DefaultShipFactory.createShip(ShipType.Frigate, ConcretePosition(1, 1), Vertical).value
    val board    = MockPlayerBoard(Set(ship))
    val position = ConcretePosition(1, 1)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isRight, "Expected to find a ship at the specified position")
    result.value shouldBe ship
  }

  test("getShipAt should return an error when no ship is found at the position") {
    val board    = MockPlayerBoard()
    val position = ConcretePosition(2, 2)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isLeft, "Expected an error when no ship is found at the position")
    result.left.value should include("No ship found at the selected position.")
  }

  test("placeShip should successfully place a ship on empty board") {
    val board    = MockPlayerBoard()
    val ship     = DefaultShipFactory.createShip(ShipType.Frigate, ConcretePosition(1, 1), Vertical).value
    val position = ConcretePosition(2, 3)

    val result = shipPositioning.placeShip(board, ship, position)

    assert(result.isRight, "Expected successful placement of ship on empty board")
    val updatedBoard = result.value
    updatedBoard.getShips should have size 1
    updatedBoard.getShips.head.getAnchor shouldBe position
  }

  test("placeShip should fail when ship overlaps with existing ship") {
    val existingShip        = DefaultShipFactory.createShip(ShipType.Frigate, ConcretePosition(1, 1), Vertical).value
    val board               = MockPlayerBoard(Set(existingShip))
    val newShip             = DefaultShipFactory.createShip(ShipType.Submarine, ConcretePosition(1, 1), Vertical).value
    val overlappingPosition = ConcretePosition(1, 1)

    val result = shipPositioning.placeShip(board, newShip, overlappingPosition)

    assert(result.isLeft, "Expected an error when placing a ship that overlaps with an existing ship")
    result.left.value should include("overlap")
  }

  test("randomPositioning should return an error if unable to place all ships") {
    val board = MockPlayerBoard()
    val ships =
      createMultipleShips(21, DefaultShipFactory.createShip(ShipType.Carrier, ConcretePosition(1, 1), Vertical).value)

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isLeft, "Expected an error when unable to randomly position all ships")
    result.left.value should include("Failed to place all ships after maximum attempts.")
  }

  test("randomPositioning should place all ships successfully") {
    val board = MockPlayerBoard()
    val ships = List(
      DefaultShipFactory.createShip(ShipType.Frigate, ConcretePosition(1, 1), Vertical).value,
      DefaultShipFactory.createShip(ShipType.Submarine, ConcretePosition(1, 1), Vertical).value
    )

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isRight, "Expected successful random positioning of ships")
    result.value.getShips should have size 2
  }
}
