package it.unibo.shipps.view

import it.unibo.shipps.model.*
import it.unibo.shipps.controller.GameController

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

  private val gridPanel = new GridPanel(PlayerBoard.size, PlayerBoard.size) {
    focusable = true
    listenTo(keys)

    reactions += {
      case KeyPressed(_, Key.R, _, _) => controller.onKeyBoardClick(controller.currentState.board.getShips.toList)
    }
  }

  contents = gridPanel

  private val doubleClickMillis = 500
  private var clickState        = ClickState(None, 0L)

  private def createButton(pos: Position, board: PlayerBoard, selectedBtn: Option[Ship]): Button =
    val btn = new Button:
      opaque = true
      border = Swing.LineBorder(java.awt.Color.LIGHT_GRAY)
      background =
        if selectedBtn.exists(_.getPositions.contains(pos)) then java.awt.Color.GRAY
        else if board.isAnyPositionOccupied(Set(pos)) then java.awt.Color.BLACK
        else java.awt.Color.CYAN
    btn

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
