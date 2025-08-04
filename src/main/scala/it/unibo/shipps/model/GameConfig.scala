package it.unibo.shipps.model

/** Represents the configuration of a game, including the ships available and their counts.
  * @param ships A map where keys are ship types and values are their counts.
  */
case class GameConfig(ships: Map[ShipType, Int])

/** A trait representing a validation and correction strategy for a game configuration. */
trait ConfigurationValidator:
  /** Validates and corrects a game configuration based on a specific rule.
    * @param config The configuration to validate.
    * @return A new, validated [[GameConfig]].
    */
  def validate(config: GameConfig): GameConfig

/** This validator ensures the total number of ship cells does not exceed a predefined percentage of the board.
  * @param maxOccupancy The maximum percentage of board cells that can be occupied by ships.
  */
class MaxOccupancyValidator(val maxOccupancy: Double) extends ConfigurationValidator:
  def validate(config: GameConfig): GameConfig =
    val boardCells     = PlayerBoard.size * PlayerBoard.size
    val maxCells       = (boardCells * maxOccupancy).toInt
    val totalShipCells = config.ships.map((shipType, amount) => shipType.length * amount).sum

    if totalShipCells <= maxCells then config
    else
      val sortedShips = ShipType.values.sortBy(-_.length)
      val correctedShips = sortedShips.foldLeft((maxCells, Map.empty[ShipType, Int])) {
        case ((remainingCells, acc), shipType) =>
          val maxCount          = config.ships.getOrElse(shipType, 0)
          val maxFit            = remainingCells / shipType.length
          val actualCount       = math.min(maxCount, maxFit)
          val newRemainingCells = remainingCells - actualCount * shipType.length
          (newRemainingCells, acc + (shipType -> actualCount))
      }._2
      GameConfig(correctedShips)

/** An object that orchestrates a series of [[ConfigurationValidator]] strategies.
  * It applies each validator in order to produce a final, corrected configuration.
  */
object ConfigurationManager:
  /** Applies a sequence of validators to a given configuration.
    * @param config The initial game configuration.
    * @param validators The sequence of validators to apply.
    * @return The final validated configuration after all rules have been applied.
    */
  def applyValidators(config: GameConfig, validators: Seq[ConfigurationValidator]): GameConfig =
    validators.foldLeft(config)((currentConfig, validator) => validator.validate(currentConfig))

/** Handles the creation of the game board with positioned ships. */
object BoardFactory:
  /** Creates a [[PlayerBoard]] with ships positioned randomly.
    * @param config The ship configuration to position.
    * @return The [[PlayerBoard]] with the ships.
    * @throws RuntimeException if ship positioning fails.
    */
  def createRandomBoard(config: GameConfig): PlayerBoard =
    val defaultPosition = Position(0, 0)
    val shipsToPlace = config.ships.flatMap((shipType, count) =>
      List.fill(count)(shipType.at(defaultPosition, Orientation.Horizontal))
    ).toList

    ShipPositioningImpl.randomPositioning(PlayerBoardBuilder.board(), shipsToPlace) match
      case Right(board) => board
      case Left(error)  => throw new RuntimeException(s"Error positioning ships: $error")
