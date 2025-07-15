package it.unibo.shipps.model

/** Represents a ship in the game. */
trait Ship:
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
  def getShape: ShipShape

  /** @return the anchor [[Position]] of the [[Ship]] */
  def getAnchor: Position

  /** @return the set of grid positions occupied by the [[Ship]] */
  def getPositions: Set[Position]

/** Predefined ship types with associated length. */
enum ShipType(val length: Int):
  case Frigate   extends ShipType(2)
  case Submarine extends ShipType(3)
  case Destroyer extends ShipType(4)
  case Carrier   extends ShipType(5)

final case class ShipImpl(
                           shipType: ShipType,
                           anchor: Position,
                           shipShape: ShipShape
                         ) extends Ship:

  override def getAnchor: Position = anchor
  override def getPositions: Set[Position] = shipShape.getOrientation match
    case Orientation.Horizontal =>
      (0 until shipShape.getLength).map(i => ConcretePosition(anchor.x() + i, anchor.y())).toSet
    case Orientation.Vertical =>
      (0 until shipShape.getLength).map(i => ConcretePosition(anchor.x(), anchor.y() + i)).toSet
  override def getShape: ShipShape = shipShape
  override def move(pos: Position): Ship = ShipImpl(shipType, pos, shipShape)
  override def rotate: Ship = ShipImpl(shipType, anchor, shipShape.rotateOrientation)


final case class ConcretePosition(xCoord: Int, yCoord: Int) extends Position: //TODO: remove
  override def x(): Int = xCoord
  override def y(): Int = yCoord


