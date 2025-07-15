package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.exceptions.{PositionOccupiedException, UnexistingShipException}
import matchers.*

/** Test suite for the PlayerBoard class. */
class PlayerBoardTest extends AnyFlatSpec with should.Matchers:
  val position: Position = ConcretePosition(2, 3)
  val ship: Ship = DefaultShipFactory
    .createShip(ShipType.Frigate, position, Orientation.Horizontal)
    .getOrElse(fail("Failed to create ship"))

  "An empty player board" should "be initialised with no ships" in:
    PlayerBoard().getShips shouldBe empty

  it should "throw UnexistingShipException if a ship is removed" in:
    a [UnexistingShipException] should be thrownBy:
      PlayerBoard().removeShip(ship).getShips shouldBe empty

  it should "allow adding a ship" in:
    PlayerBoard().addShip(ship).getShips should contain(ship)

  it should "consider any position as not occupied" in:
    val boardPositions: Set[Position] = (0 until 10).flatMap(x => (0 until 10).map(y => ConcretePosition(x, y))).toSet
    PlayerBoard().isAnyPositionOccupied(boardPositions) shouldBe false

  "A player board" should "allow removing a ship that does exist" in:
    PlayerBoard()
      .addShip(ship)
      .removeShip(ship)

  it should "update occupied positions correctly, after a ship is added" in:
    PlayerBoard()
      .addShip(ship)
      .isAnyPositionOccupied(Set(position)) shouldBe true

  it should "throw PositionOccupiedException if a ship is added to an occupied position" in:
    val board = PlayerBoard()
      .addShip(ship)
    a [PositionOccupiedException] should be thrownBy:
      board.addShip(ship)

  it should "print a nice and clear string representation" in:
    PlayerBoard(Set(ship)).toString shouldEqual (
      "\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | X | X | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n" +
        "O | O | O | O | O | O | O | O | O | O |\n"
      )