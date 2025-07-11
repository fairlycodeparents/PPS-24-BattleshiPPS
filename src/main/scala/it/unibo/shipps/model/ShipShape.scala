package it.unibo.shipps.model

/**
 * Defines the shape of a [[Ship]].
 */
trait ShipShape:
  /**
   * @return the length of the [[Ship]]
   */
  def getLength(): Int

  /**
   * @return the [[Orientation]] of the [[Ship]]
   */
  def getOrientation(): Orientation

/**
 * Represents the orientation of a [[Ship]]
 */
enum Orientation:
  case Horizontal, Vertical

  /**
   * Rotates the orientation.
   * @return the opposite [[Orientation]]
   */
  def rotate(): Orientation = this match
    case Horizontal => Vertical
    case Vertical   => Horizontal
