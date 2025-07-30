package it.unibo.shipps.view.renderer

import it.unibo.shipps.controller.{GamePhase, GameState}
import it.unibo.shipps.model.{AttackResult, PlayerBoard, Position}

/** Renderer for buttons in the game board. */
object ButtonRenderer:
  /** Returns the color for a given position based on the game state.
    * @param pos the position on the board
    * @param state the current game state
    * @return the color for the button at the specified position
    */
  def getColor(pos: Position, state: GameState): java.awt.Color = {
    state.gamePhase match
      case GamePhase.Positioning                 => positioningColor(pos, state)
      case GamePhase.Battle | GamePhase.GameOver => battleColor(pos, state)
  }

  /** Returns the text for a given position based on the game state.
    * @param pos the position on the board
    * @param state the current game state
    * @return the text for the button at the specified position
    */
  def getText(pos: Position, state: GameState): String = {
    state.gamePhase match
      case GamePhase.Battle | GamePhase.GameOver => battleText(pos, state)
      case _                                     => ""
  }

  private def positioningColor(pos: Position, state: GameState): java.awt.Color = {
    if (state.selectedShip.exists(_.positions.contains(pos)))
      ColorScheme.SELECTED_SHIP
    else if (state.board.isAnyPositionOccupied(Set(pos)))
      ColorScheme.OCCUPIED
    else
      ColorScheme.UNOCCUPIED
  }

  private def battleColor(pos: Position, state: GameState): java.awt.Color = {
    state.cellColors.getOrElse(pos, ColorScheme.UNOCCUPIED)
  }

  private def battleText(pos: Position, state: GameState): String = {
    state.attackResult.get(pos) match
      case Some(AttackResult.Miss)                                                      => "X"
      case Some(AttackResult.Hit(_) | AttackResult.Sunk(_) | AttackResult.EndOfGame(_)) => "O"
      case Some(AttackResult.AlreadyAttacked)                                           => ""
      case None                                                                         => ""
  }
