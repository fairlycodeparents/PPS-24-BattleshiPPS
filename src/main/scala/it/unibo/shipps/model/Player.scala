package it.unibo.shipps.model

/**
 * Represents a player in the game
 */
trait Player {
  /**
   * Performs an attack from the [[Player]] to the enemy [[PlayerBoard]]
   *
   * @param playerBoard the enemy board to attack
   * @param position    the position chosen by the player if human or None if bot
   * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
   */
  def makeAttack(playerBoard: PlayerBoard, position: Option[Position]): (PlayerBoard, Either[String, AttackResult])
}
