package it.unibo.shipps.model

import ShipType.*
import it.unibo.shipps.model.board.Position
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.language.postfixOps

class ShipTest extends AnyFlatSpec with should.Matchers:
  val position: Position = Position(2, 3)
  val ship: Ship         = Frigate.horizontalAt(position)

  "Ship types" should "have correct lengths" in:
    ShipType.Frigate.length should be(2)
    ShipType.Submarine.length should be(3)
    ShipType.Destroyer.length should be(4)
    ShipType.Carrier.length should be(5)

  "Ship" should "be created correctly" in:
    ship.anchor shouldBe position
    ship.orientation shouldBe ShipOrientation.Horizontal
    ship.shipType shouldBe ShipType.Frigate

  it should "be able to rotate" in:
    val rotatedShip = ship.rotate
    rotatedShip.positions shouldBe Set(position, Position(2, 4))

  it should "occupy the right positions" in:
    ship.positions shouldBe Set(position, Position(3, 3))
    val rotatedShip = ship.rotate
    rotatedShip.positions shouldBe Set(position, Position(2, 4))

  it should "be able to move" in:
    val movedShip = ship.move(Position(5, 6))
    movedShip.anchor shouldBe Position(5, 6)
