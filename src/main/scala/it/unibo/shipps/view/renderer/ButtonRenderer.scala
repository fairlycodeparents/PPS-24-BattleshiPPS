package it.unibo.shipps.view.renderer

import it.unibo.shipps.controller.{GamePhase, GameState}
import it.unibo.shipps.model.{AttackResult, PlayerBoard, Position}

object ButtonRenderer:
  def getColor(pos: Position, state: GameState): java.awt.Color = {
    state.gamePhase match
      case GamePhase.Positioning                 => positioningColor(pos, state)
      case GamePhase.Battle | GamePhase.GameOver => battleColor(pos, state)
  }

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
