package it.unibo.shipps.model

/** Represents a ship in the game. */
sealed trait Ship:
  /** Moves the ship to a new position.
    * @param pos the new anchor [[Position]]
    * @return a new instance of [[Ship]] with the updated position
    */
  def move(pos: Position): Ship

  /** Rotates the [[Orientation]] of the [[Ship]].
    * @return a new instance of [[Ship]] with the rotated orientation
    */
  def rotate: Ship

  /** @return the shape of the [[Ship]] */
  def shape: ShipShape

  /** @return the anchor [[Position]] of the [[Ship]] */
  def anchor: Position

  /** @return the set of grid positions occupied by the [[Ship]] */
  def positions: Set[Position]

/** Predefined ship types with associated length. */
enum ShipType(val length: Int):
  case Frigate   extends ShipType(2)
  case Submarine extends ShipType(3)
  case Destroyer extends ShipType(4)
  case Carrier   extends ShipType(5)

  def at(position: Position, orientation: Orientation = Orientation.Horizontal): Ship =
    ShipImpl(this, position, orientation)
  def at(pos: Position): Ship            = at(pos)
  def at(x: Int, y: Int): Ship           = at(Position(x, y))
  def verticalAt(pos: Position): Ship    = at(pos, Orientation.Vertical)
  def verticalAt(x: Int, y: Int): Ship   = at(Position(x, y), Orientation.Vertical)
  def horizontalAt(pos: Position): Ship  = at(pos, Orientation.Horizontal)
  def horizontalAt(x: Int, y: Int): Ship = at(Position(x, y), Orientation.Horizontal)

final private case class ShipImpl(
    shipType: ShipType,
    position: Position,
    orientation: Orientation
) extends Ship:
  override def anchor: Position = position
  override lazy val positions: Set[Position] = orientation match
    case Orientation.Horizontal => (0 until shipType.length).map(i => Position(anchor.x + i, anchor.y)).toSet
    case Orientation.Vertical   => (0 until shipType.length).map(i => Position(anchor.x, anchor.y + i)).toSet
  override lazy val shape: ShipShape     = ShipShapeImpl(orientation, shipType.length)
  override def move(pos: Position): Ship = copy(position = pos)
  override def rotate: Ship              = copy(orientation = orientation.rotate)
