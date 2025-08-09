package it.unibo.shipps.controller

import it.unibo.shipps.model.player.Player
import it.unibo.shipps.model.board.Position

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

  /** Determines if it's a bot turn
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @return true if it's bot's turn
    */
  def isBotTurn(turn: Turn, firstPlayer: Player, secondPlayer: Player): Boolean =
    getCurrentPlayer(turn, firstPlayer, secondPlayer).isABot

  /** Gets the current player based on turn
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @return current player
    */
  def getCurrentPlayer(turn: Turn, firstPlayer: Player, secondPlayer: Player): Player =
    turn match
      case Turn.FirstPlayer  => firstPlayer
      case Turn.SecondPlayer => secondPlayer

  /** Switches to the next turn
    * @param currentTurn current turn
    * @return next turn
    */
  def switchTurn(currentTurn: Turn): Turn =
    currentTurn match
      case Turn.FirstPlayer  => Turn.SecondPlayer
      case Turn.SecondPlayer => Turn.FirstPlayer
