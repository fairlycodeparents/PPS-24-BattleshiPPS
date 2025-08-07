package it.unibo.shipps.model

import it.unibo.shipps.model.ship.ShipType.*
import it.unibo.shipps.model.board.Position
import it.unibo.shipps.model.ship.{Orientation, Ship, ShipShapeImpl, ShipType}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class ShipTest extends AnyFunSuite:
  val position: Position = Position(2, 3)
  val ship: Ship         = Frigate.horizontalAt(position)

  test("ShipType should have correct lengths"):
    ShipType.Frigate.length should be(2)
    ShipType.Submarine.length should be(3)
    ShipType.Destroyer.length should be(4)
    ShipType.Carrier.length should be(5)

  test("Ship should be created correctly"):
    ship.anchor shouldBe position
    ship.shape shouldBe ShipShapeImpl(Orientation.Horizontal, ShipType.Frigate.length)

  test("Ship should be able to rotate"):
    val rotatedShip = ship.rotate
    rotatedShip.positions shouldBe Set(position, Position(2, 4))

  test("Ship should occupy the right positions "):
    ship.positions shouldBe Set(position, Position(3, 3))
    val rotatedShip = ship.rotate
    rotatedShip.positions shouldBe Set(position, Position(2, 4))

  test("Ship should be able to move"):
    val movedShip = ship.move(Position(5, 6))
    movedShip.anchor shouldBe Position(5, 6)
