package it.unibo.shipps.view.components

import it.unibo.shipps.controller.GameState
import it.unibo.shipps.model.Position
import it.unibo.shipps.view.renderer.ButtonRenderer

import scala.swing.{Button, Swing}

/** Factory for creating buttons. */
object ButtonFactory {
  /** Creates a button for a specific position on the game board. 
   * @param pos the position on the board
   * @param state the current game state  
   * @return the [[Button]]. */
  def createGridButton(pos: Position, state: GameState): Button = {
    new Button(ButtonRenderer.getText(pos, state)) {
      opaque = true
      border = Swing.LineBorder(java.awt.Color.LIGHT_GRAY)
      background = ButtonRenderer.getColor(pos, state)
    }
  }

  /** Creates a button for starting the game. 
   * @return the [[Button]]*/
  def createStartGameButton(): Button = {
    new Button("Start Game") {
      background = java.awt.Color.GREEN
      foreground = java.awt.Color.BLACK
    }
  }
}
