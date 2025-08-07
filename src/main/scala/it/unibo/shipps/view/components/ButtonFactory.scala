package it.unibo.shipps.view.components

import it.unibo.shipps.controller.{GameState, Turn}
import it.unibo.shipps.model.board.Position
import it.unibo.shipps.model.player.Player
import it.unibo.shipps.view.renderer.ButtonRenderer

import scala.swing.{Button, Swing}

/** Factory for creating buttons. */
object ButtonFactory {

  /** Creates a button for a specific position on the game board.
    * @param pos the position on the board
    * @param state the current game state
    * @param turn the current turn of the game
    * @return the [[Button]].
    */
  def createGridButton(pos: Position, state: GameState, turn: Turn): Button =
    new Button(ButtonRenderer.getText(pos, state, turn)) {
      opaque = true
      border = Swing.LineBorder(java.awt.Color.LIGHT_GRAY)
      background = ButtonRenderer.getColor(pos, state, turn)
    }

  /** Creates a button for starting the game.
    * @return the [[Button]]
    */
  def createStartGameButton(): Button =
    new Button("Fleet deployed!") {
      background = java.awt.Color.GREEN
      foreground = java.awt.Color.BLACK
    }
}
