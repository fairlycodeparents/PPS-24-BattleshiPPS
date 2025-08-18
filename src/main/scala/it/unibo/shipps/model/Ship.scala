package it.unibo.shipps.model

import it.unibo.shipps.model.board.Position

/** Represents a ship in the game. */
sealed trait Ship:
  /** Moves the ship to a new position.
    * @param pos the new anchor [[Position]]
    * @return a new instance of [[Ship]] with the updated position
    */
  def move(pos: Position): Ship

  /** Rotates the [[ShipOrientation]] of the [[Ship]].
    *
    * @return a new instance of [[Ship]] with the rotated orientation
    */
  def rotate: Ship

  /** @return the [[ShipOrientation]] of the [[Ship]] */
  def orientation: ShipOrientation

  /** @return the anchor [[Position]] of the [[Ship]] */
  def anchor: Position

  /** @return the set of grid positions occupied by the [[Ship]] */
  def positions: Set[Position]

  /** @return the type of the [[Ship]] */
  def shipType: ShipType

/** Predefined ship types with associated length. */
enum ShipType(val length: Int):
  case Frigate   extends ShipType(2)
  case Submarine extends ShipType(3)
  case Destroyer extends ShipType(4)
  case Carrier   extends ShipType(5)

  def at(position: Position, orientation: ShipOrientation = ShipOrientation.Horizontal): Ship =
    ShipImpl(this, position, orientation)
  def at(pos: Position): Ship            = at(pos)
  def at(x: Int, y: Int): Ship           = at(Position(x, y))
  def verticalAt(pos: Position): Ship    = at(pos, ShipOrientation.Vertical)
  def verticalAt(x: Int, y: Int): Ship   = at(Position(x, y), ShipOrientation.Vertical)
  def horizontalAt(pos: Position): Ship  = at(pos, ShipOrientation.Horizontal)
  def horizontalAt(x: Int, y: Int): Ship = at(Position(x, y), ShipOrientation.Horizontal)

/** Represents the orientation of a [[Ship]] */
enum ShipOrientation:
  case Horizontal, Vertical

  /** Rotates the orientation.
    *
    * @return the opposite [[ShipOrientation]]
    */
  def rotate: ShipOrientation = this match
    case Horizontal => Vertical
    case Vertical   => Horizontal

final private case class ShipImpl(
    shipType: ShipType,
    position: Position,
    orientation: ShipOrientation
) extends Ship:
  override def anchor: Position = position
  override lazy val positions: Set[Position] = orientation match
    case ShipOrientation.Horizontal => (0 until shipType.length).map(i => Position(anchor.col + i, anchor.row)).toSet
    case ShipOrientation.Vertical   => (0 until shipType.length).map(i => Position(anchor.col, anchor.row + i)).toSet
  override def move(pos: Position): Ship = copy(position = pos)
  override def rotate: Ship              = copy(orientation = orientation.rotate)
