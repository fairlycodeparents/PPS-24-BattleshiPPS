package it.unibo.shipps.model.board

import it.unibo.shipps.model.Ship

/** Represents the player board in the game.
  * The board is a square grid of a defined size.
  */
trait PlayerBoard:
  /** Returns the set of ships currently on the player board.
    * @return the [[Set]] of [[Ship]].
    */
  def ships: Set[Ship]

  /** Returns the set of positions that have been hit.
    * @return the [[Set]] of [[Position]] that have been hit.
    */
  def hits: Set[Position]

  /** Adds a ship to the player board.
    * @param ship the [[Ship]] to add.
    * @return a [[Right]] containing a new [[PlayerBoard]] with the ship added,
    * or a [[Left]] with an error message if the position is occupied.
    */
  def addShip(ship: Ship): Either[String, PlayerBoard]

  /** Removes a ship from the player board.
    * @param ship the [[Ship]] to be removed.
    * @return a [[Right]] containing a new [[PlayerBoard]] with the ship removed,
    * or a [[Left]] with an error message if the ship does not exist.
    */
  def removeShip(ship: Ship): Either[String, PlayerBoard]

  /** Records a hit on the player board at the specified position.
    * @param target the position where the hit occurred.
    * @return a new PlayerBoard instance
    */
  def hit(target: Position): PlayerBoard

  /** Checks if the set of positions is occupied by any element on the player board.
    * @param positions the [[Set]] of [[Position]] to check.
    * @return `true` if any position is occupied, `false` otherwise.
    */
  def isAnyPositionOccupied(positions: Set[Position]): Boolean

  /** Returns an [[Option]] containing the [[Ship]] at the specified position, if it exists.
    * @param position the position to check.
    * @return an [[Option]] containing the [[Ship]] if it exists, or `None` if no ship is at that position.
    */
  def shipAtPosition(position: Position): Option[Ship]

/** Companion object for [[PlayerBoard]]. */
object PlayerBoard:
  /** The size of the player board, which is a square grid of size 10x10. */
  val size: Int = 10

  /** Creates a new instance of [[PlayerBoard]] with the specified ships and hits.
    * @param ships the [[Set]] of [[Ship]] to initialize the board with.
    * @param hits the [[Set]] of [[Position]] to initialize the board with.
    * @return a new [[PlayerBoard]] instance.
    */
  def apply(ships: Set[Ship] = Set.empty, hits: Set[Position] = Set.empty): PlayerBoard =
    PlayerBoardImpl(ships, hits, size)

  private case class PlayerBoardImpl(ships: Set[Ship], hits: Set[Position], size: Int) extends PlayerBoard:

    override def addShip(ship: Ship): Either[String, PlayerBoard] =
      if isAnyPositionOccupied(ship.positions) then
        Left(s"One or more of the following positions are already occupied: ${ship.positions.mkString(", ")}.")
      else
        Right(PlayerBoardImpl(ships + ship, hits, size))

    override def removeShip(ship: Ship): Either[String, PlayerBoard] =
      if ships.contains(ship) then
        Right(PlayerBoardImpl(ships - ship, hits, size))
      else
        Left("The ship does not exist on the player board.")

    override def hit(target: Position): PlayerBoard =
      PlayerBoardImpl(ships, hits + target, size)

    override def isAnyPositionOccupied(positions: Set[Position]): Boolean =
      val allShipPositions = ships.flatMap(_.positions)
      positions.exists(allShipPositions.contains)

    override def shipAtPosition(position: Position): Option[Ship] =
      ships.find(_.positions.contains(position))

    /** @inheritdoc
      * @note The format is as follows:
      *       - "X" for a hit on a ship,
      *       - "S" for a ship that has not been hit,
      *       - "+" for a hit on an empty spot,
      *       - "O" for an empty spot that has not been hit.
      */
    override def toString: String =
      val allPositions =
        for
          row <- 0 until size
          col <- 0 until size
        yield Position(col, row)

      allPositions.map(pos =>
        (shipAtPosition(pos), hits.contains(pos)) match
          case (Some(_), true)  => "X"
          case (Some(_), false) => "S"
          case (None, true)     => "+"
          case (None, false)    => "O"
      ).grouped(size)
        .map(_.mkString(" | "))
        .mkString("\n", "\n", "\n")
