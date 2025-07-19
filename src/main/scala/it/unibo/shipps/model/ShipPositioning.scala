package it.unibo.shipps.model

import scala.annotation.tailrec
import scala.util.Random

/** Represents the ship positioning logic in the game. */
trait ShipPositioning:

  /** Checks if the ship is out of bounds of the player board.
    * @param ship the [[Ship]] to check
    * @return `true` if the ship is out of bounds, `false` otherwise
    */
  def isShipOutOfBounds(ship: Ship): Boolean

  /** Validates if a ship can be placed on the board.
    * @param board the [[PlayerBoard]] to validate against
    * @param ship  the [[Ship]] to validate
    * @return an [[Either]] containing validation error or unit
    */
  def validateShipPlacement(board: PlayerBoard, ship: Ship): Either[String, Unit]

  /** Change the position of a ship on the player board.
    * @param board    the [[PlayerBoard]] to place the ship on
    * @param ship     the [[Ship]] to be placed
    * @param position the [[Position]] where the ship should be placed
    * @return an [[Either]] containing an error message if the ship cannot be moved, or the updated [[PlayerBoard]]
    */
  def moveShip(board: PlayerBoard, ship: Ship, position: Position): Either[String, PlayerBoard]

  /** Places a ship on the player board at the specified position.
    * @param board the [[PlayerBoard]] to place the ship on
    * @param ship  the [[Ship]] to be placed
    * @return
    */
  def placeShip(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard]

  /** Checks if the user selected a ship or not.
    * @param board        the [[PlayerBoard]] to check against
    * @param selectedShip the [[Position]] where the user selected a ship
    * @return an [[Either]] containing an error message if the position is invalid, or the [[Ship]] if valid
    */
  def getShipAt(board: PlayerBoard, selectedShip: Position): Either[String, Ship]

  /** Randomly positions the ships on the player board.
    * @param board the [[PlayerBoard]] to position the ships on
    * @param ships the list of [[Ship]] to be positioned
    * @return an [[Either]] containing an error message if positioning fails, or the updated [[PlayerBoard]]
    */
  def randomPositioning(board: PlayerBoard, ships: List[Ship]): Either[String, PlayerBoard]

/** Companion object for [[ShipPositioning]]. */
object ShipPositioningImpl extends ShipPositioning:

  override def isShipOutOfBounds(ship: Ship): Boolean =
    ship.getPositions.exists(pos =>
      pos.x < 0 || pos.x >= PlayerBoard.size || pos.y < 0 || pos.y >= PlayerBoard.size
    )

  override def validateShipPlacement(board: PlayerBoard, ship: Ship): Either[String, Unit] =
    if isShipOutOfBounds(ship) then
      Left("Ship is out of bounds.")
    else if board.isAnyPositionOccupied(ship.getPositions) then
      Left("Ship overlaps with another ship.")
    else
      Right(())

  override def moveShip(board: PlayerBoard, ship: Ship, position: Position): Either[String, PlayerBoard] =
    for {
      boardWithoutShip <- Right(board.removeShip(ship))
      movedShip        <- Right(ship.move(position))
      updatedBoard     <- placeShip(boardWithoutShip, movedShip)
    } yield updatedBoard

  override def placeShip(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard] =
    validateShipPlacement(board, ship).map(_ => board.addShip(ship))

  override def getShipAt(board: PlayerBoard, selectedShip: Position): Either[String, Ship] =
    board.getShips
      .find(_.getPositions.contains(selectedShip))
      .toRight("No ship found at the selected position.")

  override def randomPositioning(board: PlayerBoard, ships: List[Ship]): Either[String, PlayerBoard] =
    val maxAttempts = 1000
    @tailrec
    def tryPlaceShips(playerBoard: PlayerBoard, remaining: List[Ship], attempts: Int): Either[String, PlayerBoard] = {
      if remaining.isEmpty then Right(playerBoard)
      else if attempts > maxAttempts then Left("Failed to place all ships after maximum attempts.")
      else
        val ship      = remaining.head
        val randomX   = Random.nextInt(PlayerBoard.size)
        val randomY   = Random.nextInt(PlayerBoard.size)
        val movedShip = ship.move(Position(randomX, randomY))
        if isShipOutOfBounds(movedShip) || playerBoard.isAnyPositionOccupied(movedShip.getPositions) then
          tryPlaceShips(playerBoard, remaining, attempts + 1)
        else
          tryPlaceShips(playerBoard.addShip(movedShip), remaining.tail, 0)
    }
    tryPlaceShips(board, ships, 0)
