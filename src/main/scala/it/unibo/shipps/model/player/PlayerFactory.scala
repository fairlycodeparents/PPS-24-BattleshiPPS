package it.unibo.shipps.model.player

import it.unibo.shipps.model.{AttackStrategy, RandomBotAttackStrategy}

/** Factory for creating a [[Player]] */
object PlayerFactory {

  /** Creates a human player
    * @param name the name of the player
    * @return the [[Player]]
    */
  def createHumanPlayer(name: String = "human player"): Player = HumanPlayer(name)

  /** Creates a bot player
    * @param strategy the strategy used by the bot to attack, random by default
    * @return the [[Player]]
    */
  def createBotPlayer(strategy: AttackStrategy = RandomBotAttackStrategy()): Player = BotPlayer(strategy)
}
