package it.unibo.shipps.model

import scala.util.{Either, Left, Right}
import it.unibo.shipps.model.ShipType.*

/** Factory to create [[Ship]] instances. */
trait ShipFactory:
  /** Creates a [[Ship]] of the specified type, anchor, and orientation.
    * @param ship the [[ShipType]] of the [[Ship]]
    * @param pos the initial anchor [[Position]]
    * @param orientation the initial [[Orientation]]
    * @return either an error message or the created [[Ship]]
    */
  def createShip(ship: ShipType, pos: Position, orientation: Orientation): Either[String, Ship]
  
object DefaultShipFactory extends ShipFactory:
  private def shapeFor(shipType: ShipType, orientation: Orientation): ShipShape = shipType match
    case Frigate => ShipShapeImpl(orientation, Frigate.length)
    case Destroyer => ShipShapeImpl(orientation, Destroyer.length)
    case Submarine => ShipShapeImpl(orientation, Submarine.length)
    case Carrier => ShipShapeImpl(orientation, Carrier.length)

  override def createShip(ship: ShipType, anchor: Position, orientation: Orientation): Either[String, Ship] =
    Right(ShipImpl(ship, anchor, shapeFor(ship, orientation)))