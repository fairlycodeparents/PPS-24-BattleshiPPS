package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*

import it.unibo.shipps.model.ShipType.*

import matchers.*
import scala.language.postfixOps

/** Test suite for the [[GameConfiguration]]. */
class GameConfigurationTest extends AnyFlatSpec with should.Matchers:

  val validShipAmounts: Map[ShipType, Int] = Map(
    Frigate   -> 1,
    Submarine -> 1,
    Destroyer -> 1,
    Carrier   -> 1
  )

  val invalidShipAmounts: Map[ShipType, Int] = Map(
    Frigate   -> 3,
    Submarine -> 1,
    Destroyer -> 5,
    Carrier   -> 5
  )

  "The game setup" should "allow a valid configuration with ships" in:
    val config = DefaultConfiguration(validShipAmounts)
    config.update shouldEqual config.ships

  it should "allow an empty configuration" in:
    val config = DefaultConfiguration(Map.empty)
    config.update shouldEqual config.ships

  it should "not allow a configuration exceeding the maximum ship count (half of the board cells)" in:
    val config = DefaultConfiguration(invalidShipAmounts)
    config.update should not equal config.ships

  it should "adjust the configuration to fit within the maximum ship count" in:
    val config = DefaultConfiguration(invalidShipAmounts)

    val totalShipCells  = config.update.map((ship, count) => ship.length * count).sum
    val totalBoardCells = PlayerBoard.size * PlayerBoard.size
    totalShipCells should be <= (totalBoardCells / 2)
