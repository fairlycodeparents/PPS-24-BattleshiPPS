package it.unibo.shipps.view

import it.unibo.shipps.model.*
import it.unibo.shipps.controller.{GameController, GamePhase, GameState}
import it.unibo.shipps.view.components.{ButtonFactory, GridManager}
import it.unibo.shipps.view.handler.{ClickHandler, ClickState}

import java.awt
import java.awt.event.KeyListener
import scala.swing.*
import scala.swing.MenuBar.NoMenuBar.{focusable, keys}
import scala.swing.event.*

class SimpleGui(controller: GameController) extends MainFrame:

  private final val SIZE = 600
  title = "BattleshiPPS"
  preferredSize = new Dimension(SIZE, SIZE)

  private val gridManager  = new GridManager(controller)
  private val controlPanel = createControlPanel()
  private val gridPanel    = createGridPanel()

  contents = new BorderPanel {
    layout(gridPanel) = BorderPanel.Position.Center
    layout(controlPanel) = BorderPanel.Position.South
  }

  private def createControlPanel(): FlowPanel = {
    new FlowPanel {
      hGap = 5
      vGap = 5
      border = Swing.EmptyBorder(2, 0, 2, 0)

      val startButton: Button = ButtonFactory.createStartGameButton()
      startButton.reactions += {
        case ButtonClicked(_) => controller.onStartGame()
      }
      contents += startButton
    }
  }

  private def createGridPanel(): GridPanel =
    new GridPanel(PlayerBoard.size, PlayerBoard.size) {
      focusable = true
      listenTo(keys)

      reactions += {
        case KeyPressed(_, Key.R, _, _) =>
          if (controller.state.gamePhase == GamePhase.Positioning) {
            controller.onKeyBoardClick(controller.state.board.ships.toList)
          }
      }
    }

  def update(board: PlayerBoard, selected: Option[Ship]): Unit =
    val state   = controller.state
    val buttons = gridManager.createButtons(state)

    gridPanel.contents.clear()
    gridPanel.contents ++= buttons
    gridPanel.revalidate()
    gridPanel.repaint()

    GameOverHandler.handleGameOver(this, state)

object GameOverHandler:
  def handleGameOver(parent: Window, state: GameState): Unit =
    if (state.gamePhase == GamePhase.GameOver)
      Dialog.showMessage(
        parent,
        "Game Over! Tutte le navi sono state affondate!",
        title = "Game Finished",
        Dialog.Message.Info
      )
