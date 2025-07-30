package it.unibo.shipps.model

/** Represents the configuration of a game, including the ships available and their counts. */
abstract class GameConfiguration:
  /** Updates the game configuration, checking if the new configuration is valid.
    * @return the updated configuration as a map of ship types to their counts
    */
  def update: Map[ShipType, Int]

  /** Returns the player board associated with this game configuration.
    * @return the player board
    */
  def board: PlayerBoard

/** Represents the default game configuration with a specified number of ships.
  * @param ships a map where keys are ship types and values are their counts
  * @note The ships are positioned randomly on the board
  */
case class DefaultConfiguration(ships: Map[ShipType, Int]) extends GameConfiguration:
  private val defaultPosition: Position = Position(0, 0)

  override def update: Map[ShipType, Int] =
    val totalShipCells = ships.map((t, c) => t.length * c).sum
    val boardCells     = PlayerBoard.size * PlayerBoard.size
    val maxCells       = boardCells / 2

    if totalShipCells <= maxCells then ships
    else
      var remainingCells = maxCells
      val sortedShips    = ShipType.values.sortBy(-_.length)
      val corrected = sortedShips.map(shipType =>
        val maxCount    = ships.getOrElse(shipType, 0)
        val maxFit      = remainingCells / shipType.length
        val actualCount = math.min(maxCount, maxFit)
        remainingCells -= actualCount * shipType.length
        shipType -> actualCount
      ).toMap
      corrected

  override def board: PlayerBoard = ShipPositioningImpl.randomPositioning(
    PlayerBoardBuilder.board(),
    ships.flatMap((shipType, count) => List.fill(count)(shipType.at(defaultPosition))).toList
  ) match
    case Right(board) => board
    case Left(error)  => throw new RuntimeException(s"Errore nel posizionamento delle navi: $error")
