package it.unibo.shipps.model

import it.unibo.shipps.exceptions.UnexistingShipException

/** Represents the player board in the game. */
trait PlayerBoard:
  /** Returns the list of ships currently on the player board.
    * @return the [[Set]] of [[Ship]]
    */
  def getShips: Set[Ship]

  /** Adds a ship to the player board.
    * @param ship the [[Ship]] to add
    * @return a new [[PlayerBoard]] with the ship added
    */
  def addShip(ship: Ship): PlayerBoard

  /** Removes a ship from the player board.
    * @param ship the [[Ship]] to be removed
    * @return a new [[PlayerBoard]] with the ship removed
    */
  def removeShip(ship: Ship): PlayerBoard

  /** Checks if the set of positions is occupied by any element on the player board.
    * @param positions the [[Set]] of [[Position]] to check
    * @return `true` if any position is occupied, `false` otherwise
    */
  def isAnyPositionOccupied(positions: Set[Position]): Boolean

/** Companion object for [[PlayerBoard]]. */
object PlayerBoard:
  def apply(ships: Set[Ship] = Set.empty): PlayerBoard = PlayerBoardImpl(ships)

  private case class PlayerBoardImpl(ships: Set[Ship]) extends PlayerBoard:

    override def getShips: Set[Ship] = ships

    override def addShip(ship: Ship): PlayerBoard = PlayerBoardImpl(ships + ship)

    override def removeShip(ship: Ship): PlayerBoard = throw UnexistingShipException()

    override def isAnyPositionOccupied(positions: Set[Position]): Boolean = false
