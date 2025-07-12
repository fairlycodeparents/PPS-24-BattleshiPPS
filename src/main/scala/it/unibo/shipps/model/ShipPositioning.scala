package it.unibo.shipps.model

/** Represents the ship positioning logic in the game. */
trait ShipPositioning:

  /** Checks if the given position is valid for placing a ship on the player board.
    * @param board the [[PlayerBoard]] to check against
    * @param selectedPosition the [[Position]] where the ship is to be placed
    * @param shipToAdd the [[Ship]] to be placed
    * @return `true` if the position is valid, `false` otherwise
    */
  def isPositionValid(board: PlayerBoard, selectedPosition: Position, shipToAdd: Ship): Boolean

  /** Places a ship on the player board at the specified position.
    * @param board
    * @param ship
    * @param position
    * @return
    */
  def placeShip(board: PlayerBoard, ship: Ship, position: Position): PlayerBoard

  /** Checks if the user selected a ship or not.
    * @param board the [[PlayerBoard]] to check against
    * @param selectedShip the [[Position]] where the user selected a ship
    * @return an [[Either]] containing an error message if the position is invalid, or the [[Ship]] if valid
    */
  def checkShip(board: PlayerBoard, selectedShip: Position): Either[String, Ship]

  /** Randomly positions the ships on the player board.
    * @param board the [[PlayerBoard]] to position the ships on
    * @param ships the list of [[Ship]] to be positioned
    * @return an [[Either]] containing an error message if positioning fails, or the updated [[PlayerBoard]]
    */
  def randomPositioning(board: PlayerBoard, ships: List[Ship]): Either[String, PlayerBoard]

  /** Saves the position of a ship on the player board.
    * @param board the [[PlayerBoard]] to save the ship position on
    * @param ship the [[Ship]] to be saved
    * @return an [[Either]] containing an error message if saving fails, or the updated [[PlayerBoard]]
    */
  def savePosition(board: PlayerBoard, ship: Ship): Either[String, PlayerBoard]
