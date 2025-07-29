package it.unibo.shipps.view

import it.unibo.shipps.model.*
import it.unibo.shipps.controller.{GameController, GamePhase, GameState}

import java.awt
import java.awt.event.KeyListener
import scala.swing.*
import scala.swing.MenuBar.NoMenuBar.{focusable, keys}
import scala.swing.event.*

case class ClickState(pos: Option[Position], time: Long)

class SimpleGui(controller: GameController) extends MainFrame:

  private final val SIZE = 600
  title = "BattleshiPPS"
  preferredSize = new Dimension(SIZE, SIZE)

  private val controlPanel = new FlowPanel {
    hGap = 5
    vGap = 5
    border = Swing.EmptyBorder(2, 0, 2, 0)
    contents += createStartGameButton()
  }

  private val gridPanel = new GridPanel(PlayerBoard.size, PlayerBoard.size) {
    focusable = true
    listenTo(keys)

    reactions += {
      case KeyPressed(_, Key.R, _, _) =>
        if controller.state.gamePhase == GamePhase.Positioning then
          controller.onKeyBoardClick(controller.state.board.ships.toList)
    }
  }

  contents = new BorderPanel {
    layout(gridPanel) = BorderPanel.Position.Center
    layout(controlPanel) = BorderPanel.Position.South
  }

  private val doubleClickMillis = 500
  private var clickState        = ClickState(None, 0L)

  private def createStartGameButton(): Button =
    val btn = new Button("Start Game") {
      background = java.awt.Color.GREEN
      foreground = java.awt.Color.BLACK
    }

    listenTo(btn)
    reactions += {
      case ButtonClicked(`btn`) => controller.onStartGame()
    }
    btn

  private def getButtonColor(buttonPosition: Position, state: GameState): java.awt.Color =
    state.gamePhase match
      case GamePhase.Positioning =>
        if state.selectedShip.exists(_.positions.contains(buttonPosition)) then
          java.awt.Color.YELLOW
        else if state.board.isAnyPositionOccupied(Set(buttonPosition)) then
          java.awt.Color.BLACK
        else
          java.awt.Color.CYAN

      case GamePhase.Battle | GamePhase.GameOver =>
        state.attackResult.get(buttonPosition) match
          case Some(AttackResult.Miss) =>
            java.awt.Color.LIGHT_GRAY
          case Some(AttackResult.Hit(_)) =>
            java.awt.Color.ORANGE
          case Some(AttackResult.Sunk(_)) =>
            java.awt.Color.RED
          case Some(AttackResult.EndOfGame(_)) =>
            java.awt.Color.RED
          case Some(AttackResult.AlreadyAttacked) =>
            java.awt.Color.LIGHT_GRAY
          case None =>
            java.awt.Color.CYAN

  private def createButton(buttonPosition: Position, board: PlayerBoard, selectedButton: Option[Ship]): Button =
    val btn = new Button(getButtonText(buttonPosition, controller.state)):
      opaque = true
      border = Swing.LineBorder(java.awt.Color.LIGHT_GRAY)
      background = getButtonColor(buttonPosition, controller.state)
    btn

  private def getButtonText(buttonPosition: Position, state: GameState): String =
    state.gamePhase match
      case GamePhase.Battle | GamePhase.GameOver =>
        state.attackResult.get(buttonPosition) match
          case Some(AttackResult.Miss)            => "X"
          case Some(AttackResult.Hit(_))          => "O"
          case Some(AttackResult.Sunk(_))         => "O"
          case Some(AttackResult.EndOfGame(_))    => "O"
          case Some(AttackResult.AlreadyAttacked) => "X"
          case None                               => ""
      case _ => ""

  def update(board: PlayerBoard, selected: Option[Ship]): Unit =
    val buttons =
      for
        y <- 0 until PlayerBoard.size
        x <- 0 until PlayerBoard.size
      yield
        val pos = Position(x, y)
        val btn = createButton(pos, board, selected)
        listenTo(btn)
        reactions += {
          case ButtonClicked(`btn`) =>
            val now = System.currentTimeMillis()
            if clickState.pos.contains(pos) && (now - clickState.time < doubleClickMillis) then
              controller.onCellDoubleClick(pos)
              clickState = ClickState(None, 0)
            else
              controller.onCellClick(pos)
              clickState = ClickState(Some(pos), now)
        }
        btn
    gridPanel.contents.clear()
    gridPanel.contents ++= buttons
    gridPanel.revalidate()
    gridPanel.repaint()
    if controller.state.gamePhase == GamePhase.GameOver then
      Dialog.showMessage(
        this,
        "Game Over! Tutte le navi sono state affondate!",
        title = "Game Finished",
        Dialog.Message.Info
      )
