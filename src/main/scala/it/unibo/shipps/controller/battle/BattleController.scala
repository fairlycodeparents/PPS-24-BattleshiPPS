package it.unibo.shipps.controller.battle

import it.unibo.shipps.controller.{GamePhase, GameState}
import it.unibo.shipps.model.{BattleLogic, Turn}
import it.unibo.shipps.model.board.Position
import it.unibo.shipps.model.player.Player

/** BattleController handles the battle phase of the game, processing player actions. */
object BattleController:

  /** Result of a battle turn containing new state and messages */
  case class BattleResult(newState: GameState, messages: List[String], gameOver: Boolean)

  /** Processes a human player's battle click
    * @param gameState current game state
    * @param player the attacking player
    * @param turn current turn
    * @param position target position
    * @return battle result with updated state
    */
  def processHumanAttack(
      gameState: GameState,
      player: Player,
      turn: Turn,
      position: Position
  ): BattleResult =
    val clickResult = BattleLogic.processBattleClick(gameState, player, turn, Some(position))
    val gameOver    = clickResult.newState.gamePhase == GamePhase.GameOver
    BattleResult(clickResult.newState, clickResult.messages, gameOver)

  /** Processes a bot player's battle turn
    * @param gameState current game state
    * @param botPlayer the bot player
    * @param turn current turn
    * @return battle result with updated state
    */
  def processBotAttack(gameState: GameState, botPlayer: Player, turn: Turn): BattleResult =
    val clickResult = BattleLogic.processBattleClick(gameState, botPlayer, turn, None)
    val gameOver    = clickResult.newState.gamePhase == GamePhase.GameOver
    BattleResult(clickResult.newState, clickResult.messages, gameOver)
