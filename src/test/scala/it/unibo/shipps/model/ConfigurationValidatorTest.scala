package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import matchers.*
import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.model.board.PlayerBoard

import scala.language.postfixOps

/** Test suite for the [[ConfigurationValidator]]. */
class ConfigurationValidatorTest extends AnyFlatSpec with should.Matchers:

  private val validator = new MaxOccupancyValidator(maxOccupancy = 0.5)

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

  "The validator" should "return the same configuration if it is valid" in:
    val config = GameConfig(validShipAmounts)
    validator.validate(config).ships shouldEqual config.ships

  it should "return an empty configuration if the initial configuration is empty" in:
    val emptyConfig = GameConfig(Map.empty)
    validator.validate(emptyConfig).ships shouldEqual emptyConfig.ships

  it should "not return the original configuration if it exceeds the maximum ship count" in:
    val config = GameConfig(invalidShipAmounts)
    validator.validate(config).ships should not equal config.ships

  it should "correct the configuration to fit within the maximum ship count" in:
    val config          = GameConfig(invalidShipAmounts)
    val correctedConfig = validator.validate(config)

    val totalShipCells  = correctedConfig.ships.map((ship, count) => ship.length * count).sum
    val totalBoardCells = PlayerBoard.size * PlayerBoard.size
    totalShipCells should be <= (totalBoardCells / 2)
