package it.unibo.shipps.model

import it.unibo.shipps.model.board.{PlayerBoard, Position}

import scala.util.Random

/** Represents an attack strategy for players */
trait AttackStrategy {

  /** Executes an attack according to its [[AttackStrategy]]
    * @param playerBoard the enemy board to attack
    * @param position the position to attack by the player if human or None if bot
    * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
    */
  def execute(playerBoard: PlayerBoard, position: Option[Position]): (PlayerBoard, Either[String, AttackResult])
}

/** An attack strategy that first tries to attack adjacent positions to already hit positions.
  * If no such positions are available, it falls back to the other attack strategies.
  */
trait TargetAlreadyHitStrategy extends AttackStrategy:

  /** Gets the adjacent positions to a given one.
    * @param pos the [[Position]] to get adjacent positions for
    * @return a [[List]] of adjacent [[Position]]
    */
  private def getAdjacentPositions(pos: Position): List[Position] =
    val Position(x, y) = pos
    List((-1, 0), (1, 0), (0, -1), (0, 1))
      .map { case (dx, dy) => Position(x + dx, y + dy) }
      .filter(p =>
        p.row >= 0
          && p.col >= 0
          && p.row < PlayerBoard.size
          && p.col < PlayerBoard.size
      )

  /** Gets the first adjacent position to attack that is not already hit.
    * @param board the [[PlayerBoard]] to check for hits
    * @return an [[Option]] containing the first adjacent [[Position]] to attack, or [[None]] if no such position exists
    */
  private def getAdjacentToAttack(board: PlayerBoard): Option[Position] =
    val incompleteHits = board.hits.filter(pos =>
      board.shipAtPosition(pos).exists(!_.positions.subsetOf(board.hits))
    )
    val directionalHits = incompleteHits.filter(hit =>
      getAdjacentPositions(hit).exists(board.hits.contains)
    )
    val sourceHits = if (directionalHits.nonEmpty) directionalHits else incompleteHits
    Random.shuffle(sourceHits.flatMap(getAdjacentPositions).diff(board.hits)).headOption

  /** @inheritdoc */
  abstract override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match
    case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
    case None =>
      getAdjacentToAttack(playerBoard) match
        case Some(value) =>
          ShipAttack.attack(playerBoard, value)
        case None =>
          super.execute(playerBoard, position)

/** Represents the [[AttackStrategy]] of a human [[Player]] */
case class HumanAttackStrategy() extends AttackStrategy {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match
    case Some(pos) => ShipAttack.attack(playerBoard, pos)
    case None      => (playerBoard, Left("Position is required for a human attack"))
}

/** Represents the [[AttackStrategy]] of a basic bot [[Player]] */
class RandomBotAttackStrategy extends AttackStrategy {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match
    case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
    case None      => ShipAttack.attack(playerBoard, generateRandomPosition)

  private def generateRandomPosition: Position =
    val xValue = Random.nextInt(PlayerBoard.size)
    val yValue = Random.nextInt(PlayerBoard.size)
    Position(xValue, yValue)
}

class AverageBotAttackStrategy extends RandomBotAttackStrategy with TargetAlreadyHitStrategy

/** A trait for calculating the weight of a position based on existing hits. */
trait PositionWeighting:

  /** Calculates a score (or weight) for a given position based on existing hits.
    * A higher score indicates a more desirable position to target.
    * @param pos The position to evaluate.
    * @param hits The set of positions already hit.
    * @param boardSize The size of the game board.
    * @return The calculated weight as an Int.
    */
  def calculateWeight(pos: Position, hits: Set[Position], boardSize: Int): Int

/** A position weighting strategy that calculates the weight based on the minimum distance to existing hits. */
class MaxMinPositionWeighting extends PositionWeighting:

  /** @inheritdoc */
  override def calculateWeight(pos: Position, hits: Set[Position], boardSize: Int): Int =
    if hits.isEmpty then
      Int.MaxValue
    else
      hits.map(pos.distanceTo).min

/** Represents an attack strategy that uniformly distributes attacks across the board. Based on the distance to already
  * hit positions.
  * @param positionWeighting The strategy to calculate the weight of positions based on existing hits.
  */
class UniformDistributionStrategy(positionWeighting: PositionWeighting) extends AttackStrategy:

  /** @inheritdoc */
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) =
    position match
      case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
      case None =>
        val allPositions =
          for
            x <- 0 until PlayerBoard.size
            y <- 0 until PlayerBoard.size
          yield Position(x, y)
        val unhitPositions = allPositions.filterNot(playerBoard.hits.contains)

        if unhitPositions.isEmpty then (playerBoard, Left("No positions left to attack"))
        else
          val weights = unhitPositions.map(pos =>
            MaxMinPositionWeighting().calculateWeight(pos, playerBoard.hits, PlayerBoard.size)
          )
          val maxWeight      = weights.max
          val bestPositions  = unhitPositions.zip(weights).filter(_._2 == maxWeight).map(_._1)
          val chosenPosition = bestPositions(Random.nextInt(bestPositions.length))
          ShipAttack.attack(playerBoard, chosenPosition)

/** An advanced bot attack strategy that combines uniform distribution with targeting already hit positions. */
class AdvancedBotAttackStrategy
    extends UniformDistributionStrategy(MaxMinPositionWeighting())
    with TargetAlreadyHitStrategy
