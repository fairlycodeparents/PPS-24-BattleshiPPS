package it.unibo.shipps.controller

import it.unibo.shipps.model.player.Player
import it.unibo.shipps.model.board.Position
import it.unibo.shipps.model.{Turn, TurnLogic}
import it.unibo.shipps.controller.GameStateManager.GameActionResult
import it.unibo.shipps.controller.utils.DelayedExecutor
import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.handler.TurnDialogHandler

import javax.swing.Timer
import scala.swing.Swing

class BotTurnHandler(controller: GameController):

  /** Handles the bot's turn logic
    * @param state current game state
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @return
    */
  private def handleBotTurn(
      state: GameState,
      view: SimpleGui,
      turn: Turn,
      firstPlayer: Player,
      secondPlayer: Player
  ): GameActionResult =
    GameStateManager.handleBotTurn(state, view, turn, firstPlayer, secondPlayer)

  /** Schedules a bot move
    * @param state current game state
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    */
  def scheduleBotMove(state: GameState, view: SimpleGui, turn: Turn, firstPlayer: Player, secondPlayer: Player): Unit =
    DelayedExecutor.runLater() {
      val result = handleBotTurn(state, view, turn, firstPlayer, secondPlayer)
      controller.applyGameActionResult(result)
    }
