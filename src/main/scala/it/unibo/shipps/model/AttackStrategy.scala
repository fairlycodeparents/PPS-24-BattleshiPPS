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

/** A mixin for the generation of a random position */
trait RandomPositionGenerator {
  protected def generateRandomPosition: Position =
    val xValue = Random.nextInt(10)
    val yValue = Random.nextInt(10)
    Position(xValue, yValue)
}

/** A mixin for calculating adjacent positions */
trait AdjacentPositionsCalculator {
  def getAdjacentPositions(pos: Position): List[Position] =
    val Position(x, y) = pos
    List((-1, 0), (1, 0), (0, -1), (0, 1))
      .map((dx, dy) => Position(x + dx, y + dy))
      .filter(p => p.x >= 0 && p.y >= 0)
}

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
case class RandomBotAttackStrategy() extends AttackStrategy with RandomPositionGenerator {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match
    case Some(pos) => (playerBoard, Left("Position should not be required for a bot attack"))
    case None      => ShipAttack.attack(playerBoard, generateRandomPosition)
}

case class AverageBotAttackStrategy() extends AttackStrategy
    with RandomPositionGenerator
    with AdjacentPositionsCalculator {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = ???
}
