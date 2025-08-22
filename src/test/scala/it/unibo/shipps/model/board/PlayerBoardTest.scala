package it.unibo.shipps.model.board

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.{Ship, ShipType}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.board.BoardCoordinates.*
import it.unibo.shipps.model.board.ShipPlacementDSL.*
import ShipType.*
import matchers.*

import scala.language.postfixOps

class PlayerBoardTest extends AnyFlatSpec with should.Matchers:

  val position: Position = C(4)
  val ship: Ship         = Frigate horizontalAt position

  val emptyBoard: PlayerBoard = PlayerBoard()
  val boardWithShip: PlayerBoard = PlayerBoardBuilder.board(
    place a Frigate at C(4) horizontal
  )

  "An empty player board" should "be initialised with no ships" in:
    emptyBoard.ships shouldBe empty

  it should "return an error message when a ship is removed" in:
    emptyBoard.removeShip(ship).isLeft shouldBe true

  it should "allow adding a ship" in:
    emptyBoard.addShip(ship).getOrElse(fail()).ships should contain(ship)

  it should "consider any position as not occupied" in:
    val boardPositions: Set[Position] =
      (0 until PlayerBoard.size).flatMap(x =>
        (0 until PlayerBoard.size).map(y => Position(x, y))
      ).toSet
    emptyBoard.isAnyPositionOccupied(boardPositions) shouldBe false

  it should "return an empty set of hit positions" in:
    emptyBoard.hits shouldBe empty

  "A player board" should "allow removing a ship that does exist" in:
    val boardWithoutShip = boardWithShip.removeShip(ship)
    boardWithoutShip.getOrElse(fail()).ships shouldBe empty

  it should "update occupied positions correctly, after a ship is added" in:
    boardWithShip.isAnyPositionOccupied(Set(position)) shouldBe true

  it should "return a Left with an error message if a ship is added to an occupied position" in:
    val result = boardWithShip.addShip(ship)
    result.isLeft shouldBe true

  it should "return the ship at a specific position" in:
    boardWithShip.shipAtPosition(position) shouldEqual Some(ship)

  it should "return an empty optional if no ship is at the specified position" in:
    emptyBoard.shipAtPosition(position) shouldEqual None

  it should "print a nice and clear string representation" in:
    boardWithShip.toString shouldEqual (
      "\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | S | S | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n"
    )

  it should "return a new board with the updated hits after a hit" in:
    val newBoard = boardWithShip.hit(position)
    newBoard.hits should contain(position)

  it should "update the string representation correctly after a hit on a ship" in:
    val newBoard = boardWithShip.hit(position)
    newBoard.toString shouldEqual (
      "\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | X | S | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n"
    )

  it should "update the string representation correctly after a hit on an empty spot" in:
    val newBoard = boardWithShip.hit(A(1))
    newBoard.toString shouldEqual (
      "\n" +
        "+ | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | S | S | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n" +
        "O | O | O | O | O | O | O | O | O | O\n"
    )
