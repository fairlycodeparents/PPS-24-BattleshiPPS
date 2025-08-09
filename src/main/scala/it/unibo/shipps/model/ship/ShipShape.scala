package it.unibo.shipps.model.ship

/** Defines the shape of a [[Ship]]. */
trait ShipShape:
  /** @return the length of the [[Ship]] */
  def getLength: Int

  /** @return the [[Orientation]] of the [[Ship]] */
  def getOrientation: Orientation

  /** Rotates the [[Orientation]] of the [[ShipShape]].
    *
    * @return a new [[ShipShape]] with the rotated orientation
    */
  def rotateOrientation: ShipShape

final case class ShipShapeImpl(
    orientation: Orientation,
    length: Int
) extends ShipShape:

  override def getLength: Int               = length
  override def getOrientation: Orientation  = orientation
  override def rotateOrientation: ShipShape = ShipShapeImpl(orientation.rotate, length)

/** Represents the orientation of a [[Ship]] */
enum Orientation:
  case Horizontal, Vertical

  /** Rotates the orientation.
    * @return the opposite [[Orientation]]
    */
  def rotate: Orientation = this match
    case Horizontal => Vertical
    case Vertical   => Horizontal
