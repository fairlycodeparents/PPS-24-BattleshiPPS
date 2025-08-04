package it.unibo.shipps.view.renderer

import it.unibo.shipps.controller.{GamePhase, GameState, Turn}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.AttackResult

/** Renderer for buttons in the game board. */
object ButtonRenderer:
  /** Returns the color for a given position based on the game state.
    * @param pos the position on the board
    * @param state the current game state
    * @param turn the current turn of the game
    * @return the color for the button at the specified position
    */
  def getColor(pos: Position, state: GameState, turn: Turn): java.awt.Color = {
    state.gamePhase match
      case GamePhase.Positioning                 => positioningColor(pos, state, turn)
      case GamePhase.Battle | GamePhase.GameOver => battleColor(pos, state, turn)
  }

  /** Returns the text for a given position based on the game state.
    * @param pos the position on the board
    * @param state the current game state
    * @param turn the current turn of the game
    * @return the text for the button at the specified position
    */
  def getText(pos: Position, state: GameState, turn: Turn): String = {
    state.gamePhase match
      case GamePhase.Battle | GamePhase.GameOver => battleText(pos, state, turn)
      case _                                     => ""
  }

  private def positioningColor(pos: Position, state: GameState, turn: Turn): java.awt.Color = {
    if turn == Turn.FirstPlayer then
      if (state.selectedShip.exists(_.positions.contains(pos)))
        ColorScheme.SELECTED_SHIP
      else if (state.board.isAnyPositionOccupied(Set(pos)))
        ColorScheme.OCCUPIED
      else
        ColorScheme.UNOCCUPIED
    else if (state.selectedShip.exists(_.positions.contains(pos)))
      ColorScheme.SELECTED_SHIP
    else if (state.enemyBoard.isAnyPositionOccupied(Set(pos)))
      ColorScheme.OCCUPIED
    else
      ColorScheme.UNOCCUPIED
  }

  private def battleColor(pos: Position, state: GameState, turn: Turn): java.awt.Color = {
    turn match {
      case Turn.FirstPlayer =>
        state.cellColors.getOrElse(pos, ColorScheme.UNOCCUPIED)
      case Turn.SecondPlayer =>
        state.enemyCellColors.getOrElse(pos, ColorScheme.UNOCCUPIED)
    }
  }

  private def battleText(pos: Position, state: GameState, turn: Turn): String =
    turn match {
      case Turn.FirstPlayer =>
        state.attackResult.get(pos) match
          case Some(AttackResult.Miss)                                                      => "X"
          case Some(AttackResult.Hit(_) | AttackResult.Sunk(_) | AttackResult.EndOfGame(_)) => "O"
          case Some(AttackResult.AlreadyAttacked)                                           => ""
          case None                                                                         => ""
      case Turn.SecondPlayer =>
        state.enemyAttackResult.get(pos) match
          case Some(AttackResult.Miss)                                                      => "X"
          case Some(AttackResult.Hit(_) | AttackResult.Sunk(_) | AttackResult.EndOfGame(_)) => "O"
          case Some(AttackResult.AlreadyAttacked)                                           => ""
          case None                                                                         => ""
    }
