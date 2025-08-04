package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.board.exceptions.{PositionOccupiedException, UnexistingShipException}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import matchers.*

/** Test suite for the PlayerBoard class. */
class PlayerBoardTest extends AnyFlatSpec with should.Matchers:
  val position: Position = Position(2, 3)
  val ship: Ship         = ShipType.Frigate.horizontalAt(position)

  "An empty player board" should "be initialised with no ships" in:
    PlayerBoard().ships shouldBe empty

  it should "throw UnexistingShipException if a ship is removed" in:
    a[UnexistingShipException] should be thrownBy:
      PlayerBoard().removeShip(ship).ships shouldBe empty

  it should "allow adding a ship" in:
    PlayerBoard().addShip(ship).ships should contain(ship)

  it should "consider any position as not occupied" in:
    val boardPositions: Set[Position] = (0 until PlayerBoard.size).flatMap(x =>
      (0 until PlayerBoard.size).map(y => Position(x, y))
    ).toSet
    PlayerBoard().isAnyPositionOccupied(boardPositions) shouldBe false

  it should "return an empty set of hit positions" in:
    PlayerBoard().hits shouldBe empty

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
    a[PositionOccupiedException] should be thrownBy:
      board.addShip(ship)

  it should "return the ship at a specific position" in:
    val board = PlayerBoard()
      .addShip(ship)
      .shipAtPosition(position) shouldEqual Some(ship)

  it should "return an empty optional if no ship is at the specified position" in:
    PlayerBoard().shipAtPosition(position) shouldEqual None

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

  it should "return an empty set of damaged ships when no hits have occurred" in:
    PlayerBoard().hits shouldBe empty

  it should "return a set of damaged ships" in:
    PlayerBoard()
      .addShip(ship)
      .hit(position)
      .hits shouldEqual Set(position)
