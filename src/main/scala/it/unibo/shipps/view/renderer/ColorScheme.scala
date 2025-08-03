package it.unibo.shipps.view.renderer

import java.awt.Color

/** Color scheme for the game board.
  * Defines colors for different states of the game board cells.
  */
object ColorScheme {
  val UNOCCUPIED: Color    = java.awt.Color.CYAN
  val SELECTED_SHIP: Color = java.awt.Color.YELLOW
  val OCCUPIED: Color      = java.awt.Color.BLACK
  val MISS: Color          = java.awt.Color.LIGHT_GRAY
  val HIT: Color           = java.awt.Color.ORANGE
  val SUNK: Color          = java.awt.Color.RED
}
