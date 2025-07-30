package it.unibo.shipps.view.components

import it.unibo.shipps.Main.{listenTo, reactions}
import it.unibo.shipps.controller.{GameController, GameState}
import it.unibo.shipps.model.{PlayerBoard, Position}
import it.unibo.shipps.view.handler.{ClickHandler, ClickState}
import it.unibo.shipps.view.renderer.ButtonRenderer

import scala.swing.event.ButtonClicked
import scala.swing.{Button, Swing}

/** Manages the grid of buttons representing the game board. Handles button clicks and updates the game state.
 * @param controller the game controller that manages the game logic
 * */
class GridManager(controller: GameController) {
  private var clickState = ClickState(None, 0L)

  /** Updates the grid buttons based on the current game state.
   * @param state the current game state
   * @return a sequence of buttons representing the game board
   */
  def createButtons(state: GameState): IndexedSeq[Button] = {
    for {
      y <- 0 until PlayerBoard.size
      x <- 0 until PlayerBoard.size
    } yield {
      val pos = Position(x, y)
      val btn = ButtonFactory.createGridButton(pos, state)

      listenTo(btn)
      reactions += {
        case ButtonClicked(`btn`) =>
          val now                        = System.currentTimeMillis()
          val (newClickState, clickType) = ClickHandler.processClick(pos, now, clickState)
          clickState = newClickState
          ClickHandler.handleClick(clickType, controller)
      }
      btn
    }
  }
}
