package it.unibo.shipps

import it.unibo.shipps.model.*
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers.*

class ShipTest extends AnyFunSuite:

  val factory: ShipFactory = DefaultShipFactory

  test("ShipType should have correct lengths"):
    ShipType.Frigate.length should be (2)
    ShipType.Submarine.length should be (3)
    ShipType.Destroyer.length should be (4)
    ShipType.Carrier.length should be (5)

  test("Ship should be created correctly"):
    factory.createShip(ShipType.Frigate, ConcretePosition(2, 3), Orientation.Horizontal) match
      case Right(ship) =>
        ship.getAnchor shouldBe ConcretePosition(2, 3)
        ship.getShape shouldBe ShipShapeImpl(Orientation.Horizontal, ShipType.Frigate.length)
      case Left(error) =>
        fail(s"Error in ship creation: $error")


  test("Ship should occupy the right positions "):
    factory.createShip(ShipType.Frigate, ConcretePosition(2, 3), Orientation.Horizontal) match
      case Right(ship) =>
        ship.getPositions shouldBe Set(ConcretePosition(2,3), ConcretePosition(3,3))
      case Left(error) =>
        fail(s"Error in ship creation: $error")

    factory.createShip(ShipType.Frigate, ConcretePosition(2, 3), Orientation.Vertical) match
      case Right(ship) =>
        ship.getPositions shouldBe Set(ConcretePosition(2, 3), ConcretePosition(2, 4))
      case Left(error) =>
        fail(s"Error in ship creation: $error")

  test("Ship should be able to rotate"):
    factory.createShip(ShipType.Frigate, ConcretePosition(2, 3), Orientation.Horizontal) match
      case Right(ship) =>
        val rotatedShip = ship.rotate
        rotatedShip.getPositions shouldBe Set(ConcretePosition(2, 3), ConcretePosition(2, 4))
      case Left(error) =>
        fail(s"Error in ship creation: $error")

  test("Ship should be able to move"):
    factory.createShip(ShipType.Frigate, ConcretePosition(2, 3), Orientation.Horizontal) match
      case Right(ship) =>
        val movedShip = ship.move(ConcretePosition(5, 6))
        movedShip.getAnchor shouldBe ConcretePosition(5, 6)
      case Left(error) =>
        fail(s"Error in ship creation: $error")
