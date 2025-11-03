package it.unibo.shipps.model.board

import it.unibo.shipps.model.{ShipOrientation, ShipPositioningImpl, ShipType}
import it.unibo.shipps.model.ShipType.*
import scala.language.postfixOps

object PlayerBoardBuilder:

  import ShipPlacementDSL.Placement

  /** Creates a [[PlayerBoard]] from a list of placements.
   * @param placements a sequence of [[Placement]] objects
   * @return an [[Either]] containing the error message or the constructed [[PlayerBoard]]
   */
  def board(placements: Placement*): Either[String, PlayerBoard] =
    val initial: Either[String, PlayerBoard] = Right(PlayerBoard())
    placements.foldLeft(initial) { (acc, placement) =>
      for {
        currentBoard <- acc
        updatedBoard <- ShipPositioningImpl.placeShip(
          currentBoard,
          placement.shipType.at(placement.start, placement.orientation)
        )
      } yield updatedBoard
    }

object ShipPlacementDSL:

  /** Represents a ship placement on the board.
    * @param shipType    the type of the ship to place
    * @param start       the starting position of the ship
    * @param orientation the orientation of the ship (horizontal or vertical)
    */
  case class Placement(shipType: ShipType, start: Position, orientation: ShipOrientation)

  /** DSL entry-point to start defining a ship placement. */
  object place:
    def a(shipType: ShipType): ShipPlacementBuilder = ShipPlacementBuilder(shipType)

  /** Builder to associate the ship with a position.
    * @param shipType the type of the ship being placed
    */
  class ShipPlacementBuilder(shipType: ShipType):
    /** Defines the starting position of the ship.
      * @param pos the starting position
      * @return a builder to specify the orientation
      */
    infix def at(pos: Position): ShipWithPositionBuilder = ShipWithPositionBuilder(shipType, pos)

  /** Builder to complete the placement definition by specifying the orientation.
    * @param shipType the type of the ship
    * @param pos      the starting position of the ship
    */
  class ShipWithPositionBuilder(shipType: ShipType, pos: Position):
    /** Creates a horizontal placement for the ship.
      * @return a [[Placement]] with horizontal orientation
      */
    def horizontal: Placement = Placement(shipType, pos, ShipOrientation.Horizontal)

    /** Creates a vertical placement for the ship.
      * @return a [[Placement]] with vertical orientation
      */
    def vertical: Placement = Placement(shipType, pos, ShipOrientation.Vertical)

object BoardCoordinates:

  /** A mapping of letters 'A' through 'J' to columns 0 through 9. */
  private val letterToColumn: Map[Char, Int] = ('A' to 'J').zipWithIndex.toMap

  /** Converts a letter and row number to a [[Position]]. */
  private object column:

    /** Creates a [[Position]] from a letter and row number.
      * @param letter the letter representing the column (A-J)
      * @param row the row number (1-10)
      * @return a [[Position]] corresponding to the letter and row
      * @throws IllegalArgumentException if the letter or row is invalid
      */
    def apply(letter: Char)(row: Int): Position =
      letterToColumn.get(letter.toUpper) match
        case Some(col) => Position(col, row - 1)
        case _         => throw new IllegalArgumentException(s"Invalid coordinate: ${letter.toUpper}$row")

  /** Helpers to define readable coordinates, e.g. C(5). */
  val A: Int => Position = column('A')
  val B: Int => Position = column('B')
  val C: Int => Position = column('C')
  val D: Int => Position = column('D')
  val E: Int => Position = column('E')
  val F: Int => Position = column('F')
  val G: Int => Position = column('G')
  val H: Int => Position = column('H')
  val I: Int => Position = column('I')
  val J: Int => Position = column('J')
