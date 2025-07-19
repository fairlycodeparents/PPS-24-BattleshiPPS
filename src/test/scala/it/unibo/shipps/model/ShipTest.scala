package it.unibo.shipps.model

import it.unibo.shipps.model.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class ShipTest extends AnyFunSuite:
  val position: Position = Position(2, 3)
  val ship: Ship         = ShipImpl(ShipType.Frigate, position, Orientation.Horizontal)

  test("ShipType should have correct lengths"):
    ShipType.Frigate.length should be(2)
    ShipType.Submarine.length should be(3)
    ShipType.Destroyer.length should be(4)
    ShipType.Carrier.length should be(5)

  test("Ship should be created correctly"):
    ship.getAnchor shouldBe position
    ship.getShape shouldBe ShipShapeImpl(Orientation.Horizontal, ShipType.Frigate.length)

  test("Ship should be able to rotate"):
    val rotatedShip = ship.rotate
    rotatedShip.getPositions shouldBe Set(position, Position(2, 4))

  test("Ship should occupy the right positions "):
    ship.getPositions shouldBe Set(position, Position(3, 3))
    val rotatedShip = ship.rotate
    rotatedShip.getPositions shouldBe Set(position, Position(2, 4))

  test("Ship should be able to move"):
    val movedShip = ship.move(Position(5, 6))
    movedShip.getAnchor shouldBe Position(5, 6)
