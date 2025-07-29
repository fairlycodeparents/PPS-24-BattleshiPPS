package it.unibo.shipps.model

/** Represents a player in the game */
trait Player {

  /** Performs an attack from the [[Player]] to the enemy [[PlayerBoard]]
    *
    * @param playerBoard the enemy board to attack
    * @param position    the position chosen by the player if human or None if bot
    * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
    */
  def makeAttack(playerBoard: PlayerBoard, position: Option[Position]): (PlayerBoard, Either[String, AttackResult])

  /** @return true if the [[Player]] is a bot */
  def isABot: Boolean
}

case class HumanPlayer(name: String) extends Player:
  override def makeAttack(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = ???
  override def isABot: Boolean = false

case class BotPlayer() extends Player:
  override def makeAttack(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = ???
  override def isABot: Boolean = true
