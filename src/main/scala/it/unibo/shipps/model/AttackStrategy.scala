package it.unibo.shipps.model

/** Represents an attack strategy for players */
trait AttackStrategy {

  /** Executes an attack according to its [[AttackStrategy]]
    * @param playerBoard the enemy board to attack
    * @param position the position to attack by the player if human or None if bot
    * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
    */
  def execute(playerBoard: PlayerBoard, position: Option[Position]): (PlayerBoard, Either[String, AttackResult])
}

/** Represents the [[AttackStrategy]] of a human [[Player]] */
case class HumanAttackStrategy() extends AttackStrategy {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = position match {
    case Some(pos) => ShipAttack.attack(playerBoard, pos)
    case None      => (playerBoard, Left("Position is required for a human attack"))
  }
}

/** Represents the [[AttackStrategy]] of a base bot [[Player]] */
case class RandomBotAttackStrategy() extends AttackStrategy {
  override def execute(
      playerBoard: PlayerBoard,
      position: Option[Position]
  ): (PlayerBoard, Either[String, AttackResult]) = ???
}
