package it.unibo.shipps.model.player

import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.{AttackResult, AttackStrategy, HumanAttackStrategy}

/** Represents a player in the game */
trait Player {

  /** Performs an attack from the [[Player]] to the enemy [[PlayerBoard]]
    *
    * @param playerBoard the enemy board to attack
    * @param position    the position chosen by the player if human or None if bot
    * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
    */
  def makeAttack(
      playerBoard: PlayerBoard,
      position: Option[Position] = Option.empty
  ): (PlayerBoard, Either[String, AttackResult])

  /** @return true if the [[Player]] is a bot */
  def isABot: Boolean
}

private[player] case class HumanPlayer(name: String = "player", strategy: AttackStrategy = HumanAttackStrategy()) extends Player:
  override def makeAttack(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = strategy.execute(playerBoard, position)
  override def isABot: Boolean = false

private[player] case class BotPlayer(strategy: AttackStrategy) extends Player:
  override def makeAttack(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = strategy.execute(playerBoard, position)
  override def isABot: Boolean = true
