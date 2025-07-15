package it.unibo.shipps.model

import scala.annotation.tailrec
import scala.util.Random

case class SimplePosition(x0: Int, y0: Int) extends Position:
  override def x(): Int = x0
  override def y(): Int = y0

/** Represents the ship positioning logic in the game. */
trait ShipPositioning:

  /** Checks if the ship can be placed on the player board without overlapping with other ships or going out of bounds.
    * @param board the [[PlayerBoard]] to check against
    * @param ship the [[Ship]] to be placed
    * @return an [[Either]] containing an error message if the placement is invalid, or `Unit` if valid
    */
  def isValidPlacement(board: PlayerBoard, ship: Ship): Either[String, Unit] =
    val positions = ship.getPositions
    if board.isAnyPositionOccupied(positions) then
      Left("Ship overlaps with another ship or is out of bounds.")
    else
      Right(())

  /** Places a ship on the player board at the specified position.
    * @param board
    * @param ship
    * @param position
    * @return
    */
  def placeShip(board: PlayerBoard, ship: Ship, position: Position): Either[String, PlayerBoard] =
    val movedShip = ship.move(position)
    isValidPlacement(board, movedShip) match
      case Left(error) => Left(error)
      case Right(_)    => Right(board.addShip(movedShip))

  /** Checks if the user selected a ship or not.
    * @param board the [[PlayerBoard]] to check against
    * @param selectedShip the [[Position]] where the user selected a ship
    * @return an [[Either]] containing an error message if the position is invalid, or the [[Ship]] if valid
    */
  def getShipAt(board: PlayerBoard, selectedShip: Position): Either[String, Ship] =
    val ships          = board.getShips
    val shipAtPosition = ships.find(ship => ship.getPositions.contains(selectedShip))
    shipAtPosition match
      case Some(ship) => Right(ship)
      case None       => Left("No ship found at the selected position.")

  /** Randomly positions the ships on the player board.
    * @param board the [[PlayerBoard]] to position the ships on
    * @param ships the list of [[Ship]] to be positioned
    * @return an [[Either]] containing an error message if positioning fails, or the updated [[PlayerBoard]]
    */
  def randomPositioning(board: PlayerBoard, ships: List[Ship]): Either[String, PlayerBoard] =
    val maxAttempts = 1000
    @tailrec
    def tryPlaceShips(b: PlayerBoard, remaining: List[Ship], attempts: Int): Either[String, PlayerBoard] =
      if remaining.isEmpty then Right(b)
      else if attempts > maxAttempts then Left("Failed to place all ships after maximum attempts.")
      else
        val ship      = remaining.head
        val movedShip = ship.move(SimplePosition(Random.nextInt(board.width), Random.nextInt(board.height)))
        isValidPlacement(b, movedShip) match
          case Right(_) =>
            tryPlaceShips(b.addShip(movedShip), remaining.tail, 0)
          case Left(_) =>
            tryPlaceShips(b, remaining, attempts + 1)
    tryPlaceShips(board, ships, 0)
