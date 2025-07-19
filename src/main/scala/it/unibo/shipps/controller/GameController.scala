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

  private def handleCellAction(pos: Position)(
    shipAction: (PlayerBoard, Ship, Position) => Either[String, PlayerBoard]
  ): Unit =
    state = state.selectedShip match
      case None =>
        positioning.getShipAt(state.board, pos) match
          case Right(ship) => state.copy(selectedShip = Some(ship))
          case Left(_)     => state
      case Some(ship) =>
        shipAction(state.board, ship, pos) match
          case Right(updatedBoard) => GameState(updatedBoard, None)
          case Left(_)             => state
    updateView()

  private def updateView(): Unit =
    Swing.onEDT(view.update(state.board, state.selectedShip))

  def onCellClick(pos: Position): Unit =
    handleCellAction(pos)(positioning.moveShip)

  def onCellDoubleClick(pos: Position): Unit =
    handleCellAction(pos) { (board, ship, _) => positioning.rotateShip(board, ship) }
