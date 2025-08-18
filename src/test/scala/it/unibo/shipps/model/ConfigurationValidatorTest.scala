package it.unibo.shipps.model

import org.scalatest.*
import flatspec.*
import matchers.*
import ShipType.*
import it.unibo.shipps.model.board.PlayerBoard

import scala.language.postfixOps

/** Test suite for the [[ConfigurationValidator]]. */
class ConfigurationValidatorTest extends AnyFlatSpec with should.Matchers:

  private val maxOccupancyValidator = new MaxOccupancyValidator(maxOccupancy = 0.5)
  private val notEmptyValidator     = new NotEmptyValidator()

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

  "The MaxOccupancyValidator" should "return the same configuration if it is valid" in:
    val config = GameConfig(validShipAmounts)
    maxOccupancyValidator.validate(config).ships shouldEqual config.ships

  it should "return an empty configuration if the initial configuration is empty" in:
    val emptyConfig = GameConfig(Map.empty)
    maxOccupancyValidator.validate(emptyConfig).ships shouldEqual emptyConfig.ships

  it should "not return the original configuration if it exceeds the maximum ship count" in:
    val config = GameConfig(invalidShipAmounts)
    maxOccupancyValidator.validate(config).ships should not equal config.ships

  it should "correct the configuration to fit within the maximum ship count" in:
    val config          = GameConfig(invalidShipAmounts)
    val correctedConfig = maxOccupancyValidator.validate(config)

    val totalShipCells  = correctedConfig.ships.map((ship, count) => ship.length * count).sum
    val totalBoardCells = PlayerBoard.size * PlayerBoard.size
    totalShipCells should be <= (totalBoardCells / 2)

  "The NotEmptyValidator" should "add at least a ship, if the configuration is empty" in:
    val emptyConfig = GameConfig(Map.empty)
    notEmptyValidator.validate(emptyConfig).ships should not equal emptyConfig.ships

  it should "return the same configuration if it is not empty" in:
    val config = GameConfig(validShipAmounts)
    notEmptyValidator.validate(config).ships shouldEqual config.ships
