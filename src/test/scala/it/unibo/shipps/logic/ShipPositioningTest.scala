package it.unibo.shipps.logic

import it.unibo.shipps.model
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import it.unibo.shipps.model.{PlayerBoard, Position, Ship, ShipPositioning, ShipShape, ShipType}
import org.scalatest.EitherValues.convertEitherToValuable
import org.scalatest.EitherValues.convertLeftProjectionToValuable

class ShipPositioningTest extends AnyFunSuite with Matchers {

  // Mock implementations per i test
  case class MockPosition(x_pos: Int, y_pos: Int) extends Position {

    /** Returns the x coordinate of the position.
      *
      * @return the x coordinate
      */
    override def x(): Int = x_pos

    /** Returns the y coordinate of the position.
      *
      * @return the y coordinate
      */
    override def y(): Int = y_pos
  }

  case class MockShipShape() extends ShipShape {

    /** @return the length of the [[Ship]] */
    override def getLength(): Int = ???

    /** @return the [[Orientation]] of the [[Ship]] */
    override def getOrientation(): model.Orientation = ???
  }

  case class MockShip(
      shipType: ShipType,
      anchor: Position,
      orientation: Orientation = Orientation.Horizontal,
      positions: Set[Position] = Set.empty
  ) extends it.unibo.shipps.model.Ship:

    def move(pos: Position): Ship = this.copy(anchor = pos)

    def rotate(): Ship =
      val newOrientation = orientation match
        case Orientation.Horizontal => Orientation.Vertical
        case Orientation.Vertical   => Orientation.Horizontal
      this.copy(orientation = newOrientation)

    def getShape(): ShipShape = MockShipShape()
    def getAnchor(): Position = anchor
    def getPositions(): Set[Position] =
      if positions.nonEmpty then positions
      else calculatePositions()

    private def calculatePositions(): Set[Position] =
      (0 until shipType.length).map { offset =>
        orientation match
          case Orientation.Horizontal => MockPosition(anchor.x() + offset, anchor.y())
          case Orientation.Vertical   => MockPosition(anchor.x(), anchor.y() + offset)
      }.toSet

  case class MockPlayerBoard(ships: Seq[Ship] = Seq.empty) extends PlayerBoard:

    def getShips: Seq[Ship] = ships

    def addShip(ship: Ship): PlayerBoard =
      this.copy(ships = ships :+ ship)

    def removeShip(ship: Ship): PlayerBoard =
      this.copy(ships = ships.filterNot(_ == ship))

    def isAnyPositionOccupied(positions: Seq[Position]): Boolean =
      val allOccupiedPositions = ships.flatMap(_.getPositions()).toSet
      positions.exists(allOccupiedPositions.contains)

    override val width: Int  = 10
    override val height: Int = 10

  enum Orientation:
    case Horizontal, Vertical

  // Test cases
  val shipPositioning: ShipPositioning = new ShipPositioning {}
  test("getShipAt should successfully return a ship at a given position") {
    val ship     = MockShip(ShipType.Frigate, MockPosition(1, 1))
    val board    = MockPlayerBoard(Seq(ship))
    val position = MockPosition(1, 1)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isRight, "Expected to find a ship at the specified position")
    result.value shouldBe ship
  }

  test("getShipAt should return an error when no ship is found at the position") {
    val board    = MockPlayerBoard()
    val position = MockPosition(2, 2)

    val result = shipPositioning.getShipAt(board, position)

    assert(result.isLeft, "Expected an error when no ship is found at the position")
    result.left.value should include("No ship found at the selected position.")
  }

  test("placeShip should successfully place a ship on empty board") {
    val board    = MockPlayerBoard()
    val ship     = MockShip(ShipType.Frigate, MockPosition(0, 0))
    val position = MockPosition(2, 3)

    val result = shipPositioning.placeShip(board, ship, position)

    assert(result.isRight, "Expected successful placement of ship on empty board")
    val updatedBoard = result.value
    updatedBoard.getShips should have size 1
    updatedBoard.getShips.head.getAnchor() shouldBe position
  }

  test("placeShip should fail when ship overlaps with existing ship") {
    val existingShip        = MockShip(ShipType.Frigate, MockPosition(1, 1))
    val board               = MockPlayerBoard(Seq(existingShip))
    val newShip             = MockShip(ShipType.Submarine, MockPosition(0, 0))
    val overlappingPosition = MockPosition(1, 1)

    val result = shipPositioning.placeShip(board, newShip, overlappingPosition)

    assert(result.isLeft, "Expected an error when placing a ship that overlaps with an existing ship")
    result.left.value should include("overlap")
  }

  test("randomPositioning should return an error if unable to place all ships") {
    val board = MockPlayerBoard()
    val ships = List(
      MockShip(ShipType.Frigate, MockPosition(0, 0)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1)),
      MockShip(ShipType.Destroyer, MockPosition(1, 1))
    )

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isLeft, "Expected an error when unable to randomly position all ships")
    result.left.value should include("Failed to place all ships after maximum attempts.")
  }

  test("randomPositioning should place all ships successfully") {
    val board = MockPlayerBoard()
    val ships = List(
      MockShip(ShipType.Frigate, MockPosition(0, 0)),
      MockShip(ShipType.Submarine, MockPosition(0, 0))
    )

    val result = shipPositioning.randomPositioning(board, ships)

    assert(result.isRight, "Expected successful random positioning of ships")
    result.value.getShips should have size 2
  }
}
