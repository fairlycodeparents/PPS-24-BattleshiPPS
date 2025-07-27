package it.unibo.shipps.model

import it.unibo.shipps.exceptions.{PositionOccupiedException, UnexistingShipException}

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

  /** Returns an [[Option]] containing the [[Ship]] at the specified position, if it exists.
    * @param position the position to check
    * @return an [[Option]] containing the [[Ship]] if it exists, or `None` if no ship is at that position
    */
  def shipAtPosition(position: Position): Option[Ship]

  /** Returns all the positions that has been hit.
    * @return the [[Set]] of [[Position]] that have been hit on the player board
    */
  def hitPositons: Set[Position]

  /** Records a hit on the player board at the specified position.
    * @param target the position where the hit occurred
    */
  def hit(target: Position): PlayerBoard

/** Companion object for [[PlayerBoard]]. */
object PlayerBoard:
  /** The size of the player board, which is a square grid of size 10x10. */
  val size: Int = 10

  /** Creates a new instance of [[PlayerBoard]] with the specified ships.
    * @param ships the [[Set]] of [[Ship]] to initialize the board with
    * @return a new [[PlayerBoard]] instance
    */
  def apply(ships: Set[Ship] = Set.empty): PlayerBoard = PlayerBoardImpl(ships, Set.empty)

  private case class PlayerBoardImpl(ships: Set[Ship], hit: Set[Position]) extends PlayerBoard:

    override def getShips: Set[Ship] = ships

    override def addShip(ship: Ship): PlayerBoard = {
      if (isAnyPositionOccupied(ship.positions)) throw PositionOccupiedException(ship.positions.head)
      else PlayerBoardImpl(ships + ship, hit)
    }

    override def removeShip(ship: Ship): PlayerBoard =
      if (ships.contains(ship)) PlayerBoardImpl(ships - ship, hit)
      else throw UnexistingShipException()

    override def isAnyPositionOccupied(positions: Set[Position]): Boolean =
      positions.exists(pos => ships.exists(ship => ship.positions.contains(pos)))

    override def shipAtPosition(position: Position): Option[Ship] =
      ships.find(_.positions.contains(position))

    override def hitPositons: Set[Position] = hit

    override def hit(target: Position): PlayerBoard = PlayerBoardImpl(ships, hit + target)

    override def toString: String =
      (0 until size).map(row =>
        (0 until size).map(col =>
          if (isAnyPositionOccupied(Set(Position(col, row)))) "X" else "O"
        ).mkString(" | ") + " |"
      ).mkString("\n", "\n", "\n")
