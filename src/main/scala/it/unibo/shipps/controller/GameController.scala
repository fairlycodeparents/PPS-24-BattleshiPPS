package it.unibo.shipps.controller

import it.unibo.shipps.model.*
import it.unibo.shipps.view.SimpleGui
import scala.swing.Swing

case class GameState(board: PlayerBoard, selectedShip: Option[Ship])

class GameController(
    initialBoard: PlayerBoard,
    val positioning: ShipPositioning,
    var view: SimpleGui
):

  private var state = GameState(initialBoard, None)
  private def handleCellClick(state: GameState, pos: Position): GameState =
    state.selectedShip match
      case None =>
        positioning.getShipAt(state.board, pos) match
          case Right(ship) => state.copy(selectedShip = Some(ship))
          case Left(_)     => state
      case Some(ship) =>
        positioning.moveShip(state.board, ship, pos) match
          case Right(updatedBoard) => GameState(updatedBoard, None)
          case Left(_)             => state

  private def handleDoubleCellClick(state: GameState, pos: Position): GameState =
    state.selectedShip match
      case None =>
        positioning.getShipAt(state.board, pos) match
          case Right(ship) => state.copy(selectedShip = Some(ship))
          case Left(_) => state
      case Some(ship) =>
        positioning.rotateShip(state.board, ship) match
          case Right(updatedBoard) => GameState(updatedBoard, None)
          case Left(_) => state

  def onCellClick(pos: Position): Unit =
    val newState = handleCellClick(state, pos)
    state = newState
    Swing.onEDT {
      view.update(state.board, state.selectedShip)
    }

  def onCellDoubleClick(pos: Position): Unit =
    val newState = handleDoubleCellClick(state, pos)
    state = newState
    Swing.onEDT {
      view.update(state.board, state.selectedShip)
    }
