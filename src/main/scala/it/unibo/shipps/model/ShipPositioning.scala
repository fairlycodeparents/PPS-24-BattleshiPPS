package it.unibo.shipps.model

import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.ship.Ship

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

  /** Rotates a ship on the player board.
    * @param board the [[PlayerBoard]] to rotate the ship on
    * @param ship  the [[Ship]] to be rotated
    * @return an [[Either]] containing an error message if the rotation fails, or the updated [[PlayerBoard]]
    */
  def rotateShip(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard]

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

  /** Checks if the ship is out of bounds of the player board.
    *
    * @param ship the [[Ship]] to check
    * @return an [[Either]] containing an error message if the ship is out of bounds, or unit if it is within bounds
    */
  private def checkBounds(ship: Ship): Either[String, Unit] =
    if !isShipOutOfBounds(ship) then Right(())
    else Left("Ship is out of bounds.")

  /** Checks if the ship overlaps with any existing ships on the board.
    *
    * @param board the [[PlayerBoard]] to check against
    * @param ship  the [[Ship]] to check for overlap
    * @return an [[Either]] containing an error message if there is an overlap, or unit if no overlap exists
    */
  private def checkOverlap(board: PlayerBoard, ship: Ship): Either[String, Unit] =
    if !board.isAnyPositionOccupied(ship.positions) then Right(())
    else Left("Ship overlaps with another ship.")

  /** Generic method to shift a ship on the board.
    * @param board           the [[PlayerBoard]] containing the ship
    * @param ship            the original [[Ship]] to transform
    * @param shift           function that shift the ship
    * @return an [[Either]]  containing error message if shift fails, or updated [[PlayerBoard]]
    */
  private def ShiftShip(
      board: PlayerBoard,
      ship: Ship,
      shift: Ship => Ship
  ): Either[String, PlayerBoard] =
    for
      boardWithoutShip <- board.removeShip(ship)
      shiftedShip      <- Right(shift(ship))
      updatedBoard     <- placeShip(boardWithoutShip, shiftedShip)
    yield updatedBoard

  override def isShipOutOfBounds(ship: Ship): Boolean =
    ship.positions.exists(pos =>
      pos.col < 0 || pos.col >= PlayerBoard.size || pos.row < 0 || pos.row >= PlayerBoard.size
    )

  override def validateShipPlacement(board: PlayerBoard, ship: Ship): Either[String, Unit] =
    for
      _ <- checkBounds(ship)
      _ <- checkOverlap(board, ship)
    yield ()

  override def placeShip(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard] =
    validateShipPlacement(board, ship).flatMap(_ => board.addShip(ship))

  override def getShipAt(board: PlayerBoard, selectedShip: Position): Either[String, Ship] =
    board.shipAtPosition(selectedShip).toRight("No ship found at the selected position.")

  override def moveShip(board: PlayerBoard, ship: Ship, position: Position): Either[String, PlayerBoard] =
    ShiftShip(board, ship, _.move(position))

  override def rotateShip(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard] =
    ShiftShip(board, ship, _.rotate)

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
        if isShipOutOfBounds(movedShip) || playerBoard.isAnyPositionOccupied(movedShip.positions) then
          tryPlaceShips(playerBoard, remaining, attempts + 1)
        else
          playerBoard.addShip(movedShip) match
            case Right(updatedBoard) => tryPlaceShips(updatedBoard, remaining.tail, 0)
            case Left(error)         => tryPlaceShips(playerBoard, remaining, attempts + 1)
    }
    tryPlaceShips(board, ships, 0)
