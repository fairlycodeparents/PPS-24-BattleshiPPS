package it.unibo.shipps.model

/**
 * Represents the player board in the game.
 */
trait PlayerBoard:
  /**
   * Returns the list of ships currently on the player board.
   * @return the [[Seq]] of [[Ship]]
   */
  def getShips: Seq[Ship]

  /**
   * Adds a ship to the player board.
   * @param ship the [[Ship]] to add
   * @return a new [[PlayerBoard]] with the ship added
   */
  def addShip(ship: Ship): PlayerBoard

  /**
   * Removes a ship from the player board.
   * @param ship the [[Ship]] to be removed
   * @return a new [[PlayerBoard]] with the ship removed
   */
  def removeShip(ship: Ship): PlayerBoard

  /**
   * Checks if the set of positions is occupied by any element on the player board.
   * @param positions the [[Seq]] of [[Position]] to check
   * @return `true` if any position is occupied, `false` otherwise
   */
  def isAnyPositionOccupied(positions: Seq[Position]): Boolean
