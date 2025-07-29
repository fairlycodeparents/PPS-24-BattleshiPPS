package it.unibo.shipps.model

object PlayerFactory{
  def createHumanPlayer(name: String): Player = HumanPlayer(name)
  def createBotPlayer: Player = BotPlayer()
}

