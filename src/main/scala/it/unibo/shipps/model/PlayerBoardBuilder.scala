package it.unibo.shipps.model

import it.unibo.shipps.exceptions.PositionOccupiedException
import it.unibo.shipps.model.ShipType.*

import scala.language.postfixOps

/** Builder for a [[PlayerBoard]] using a DSL-like syntax. */
object PlayerBoardBuilder:

  /** Represents a ship placement on the board.
    * @param shipType the type of the ship to place
    * @param start the starting position of the ship
    * @param orientation the orientation of the ship (horizontal or vertical)
    */
  case class Placement(shipType: ShipType, start: Position, orientation: Orientation)

  /** DSL entry-point to start defining a ship placement. */
  object place:
    def a(shipType: ShipType): ShipPlacementBuilder = ShipPlacementBuilder(shipType)

  /** Builder to associate the ship with a position. */
  class ShipPlacementBuilder(shipType: ShipType):
    infix def at(pos: Position): ShipWithPositionBuilder = ShipWithPositionBuilder(shipType, pos)

  /** Builder to complete the placement definition by specifying the orientation. */
  class ShipWithPositionBuilder(shipType: ShipType, pos: Position):
    /** Creates a horizontal placement for the ship.
      * @return a [[Placement]] with horizontal orientation
      */
    def horizontal: Placement = Placement(shipType, pos, Orientation.Horizontal)

    /** Creates a vertical placement for the ship.
      * @return a [[Placement]] with vertical orientation
      */
    def vertical: Placement = Placement(shipType, pos, Orientation.Vertical)

  /** Creates a [[PlayerBoard]] from a list of placements. */
  def board(placements: Placement*): PlayerBoard =
    placements.foldLeft(PlayerBoard()): (board, placement) =>
      ShipPositioningImpl.placeShip(
        board,
        ShipImpl(placement.shipType, placement.start, placement.orientation)
      ) match
        case Left(error)         => throw new RuntimeException(error)
        case Right(updatedBoard) => updatedBoard

  /* Helpers to define readable coordinates: maps letters A–J to columns 0–9. */
  object A:
    def apply(row: Int): Position = Position(0, row - 1)
  object B:
    def apply(row: Int): Position = Position(1, row - 1)
  object C:
    def apply(row: Int): Position = Position(2, row - 1)
  object D:
    def apply(row: Int): Position = Position(3, row - 1)
  object E:
    def apply(row: Int): Position = Position(4, row - 1)
  object F:
    def apply(row: Int): Position = Position(5, row - 1)
  object G:
    def apply(row: Int): Position = Position(6, row - 1)
  object H:
    def apply(row: Int): Position = Position(7, row - 1)
  object I:
    def apply(row: Int): Position = Position(8, row - 1)
  object J:
    def apply(row: Int): Position = Position(9, row - 1)
