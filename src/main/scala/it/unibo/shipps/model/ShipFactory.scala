package it.unibo.shipps.model

import scala.util.{Either, Left, Right}

/** Factory to create [[Ship]] instances. */
trait ShipFactory:
  /**
   * Creates a [[Ship]] of the specified type, anchor, and orientation.
   * @param ship the [[ShipType]] of the [[Ship]]
   * @param pos the initial anchor [[Position]]
   * @param orientation the initial [[Orientation]]
   * @return either an error message or the created [[Ship]]
   */
  def createShip(ship: ShipType, pos: Position, orientation: Orientation): Either[String, Ship]
