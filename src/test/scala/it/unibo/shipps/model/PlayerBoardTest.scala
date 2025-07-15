package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.exceptions.UnexistingShipException
import matchers.*

/** Test suite for the PlayerBoard class. */
class PlayerBoardTest extends AnyFlatSpec with should.Matchers:
  val ship: Ship = DefaultShipFactory.createShip(
    ShipType.Frigate,
    ConcretePosition(2, 3),
    Orientation.Horizontal
  ).getOrElse(fail("Failed to create ship"))

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