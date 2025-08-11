package it.unibo.shipps.view

import it.unibo.shipps.model.*
import it.unibo.shipps.controller.{GameController, GamePhase, GameState}
import it.unibo.shipps.model.board.PlayerBoard
import it.unibo.shipps.model.TurnLogic
import it.unibo.shipps.view.components.{ButtonFactory, GridManager}
import it.unibo.shipps.view.handler.{ClickHandler, ClickState}

import java.awt.Color
import java.awt.event.KeyListener
import scala.swing.*
import scala.swing.MenuBar.NoMenuBar.{focusable, keys}
import scala.swing.event.*

class SimpleGui(controller: GameController) extends MainFrame:

  private final val SIZE = 600
  title = "BattleshiPPS"
  preferredSize = new Dimension(SIZE, SIZE)

  private val turnLabel = new Label("Player 1 Turn") {
    font = new Font("SansSerif", java.awt.Font.BOLD, 16)
    foreground = new Color(0, 100, 0)
    horizontalAlignment = Alignment.Center
  }

  private val gridManager         = new GridManager(controller)
  private val startButton         = ButtonFactory.createStartGameButton()
  private val controlPanel        = createControlPanel(startButton)
  private val gridPanel           = createGridPanel()
  private val infoPanel           = createInfoPanel()

  contents = new BorderPanel {
    layout(infoPanel) = BorderPanel.Position.North
    layout(gridPanel) = BorderPanel.Position.Center
    layout(controlPanel) = BorderPanel.Position.South
  }
  controller.showTurnDialog("Player 1 - position your ships")

  private def createControlPanel(startGameButton: Button): FlowPanel = {
    new FlowPanel {
      hGap = 5
      vGap = 5
      border = Swing.EmptyBorder(2, 0, 2, 0)

      listenTo(startGameButton)
      startGameButton.reactions += {
        case ButtonClicked(_) => controller.onStartGame()
      }
      contents += startGameButton
    }
  }

  def hideStartButton(): Unit =
    startButton.visible = false

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

  private def createInfoPanel(): BoxPanel = {
    new BoxPanel(Orientation.Horizontal) {
      opaque = true
      contents += Swing.VStrut(5)
      contents += turnLabel
      contents += Swing.VStrut(5)
      val posInstruction: Label =
        new Label("<html>• Click to select/move ships<br>• Double-click to rotate<br>• Press R to randomize</html>")
      // contents += Swing.VStrut(5)
      contents += posInstruction
      contents += Swing.VGlue
      revalidate()
      repaint()
    }
  }

  private def createBattleLegendPanel(): BoxPanel = {
    def colorBox(bg: Color, overlay: Option[Color] = None): Panel = new Panel {
      preferredSize = new Dimension(20, 20)
      maximumSize = preferredSize
      minimumSize = preferredSize

      override def paintComponent(g: Graphics2D): Unit = {
        super.paintComponent(g)
        g.setColor(bg)
        g.fillRect(0, 0, size.width, size.height)
        overlay.foreach { color =>
          g.setColor(color)
          g.fillOval(4, 4, 12, 12)
        }
      }
    }

    new BoxPanel(Orientation.Vertical) {
      contents += turnLabel
      contents += Swing.VStrut(10)
      contents += new Label("Click a position to attack")
      contents += new Label("Shot legend:")
      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        colorBox(Color.YELLOW),
        new Label(" + O = Ship hit")
      )
      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        colorBox(Color.LIGHT_GRAY),
        new Label(" + X = Miss")
      )
      contents += new FlowPanel(FlowPanel.Alignment.Left)(
        colorBox(Color.RED),
        new Label(" + O = Ship sunk")
      )
      border = Swing.EmptyBorder(10, 0, 0, 0)
    }
  }

  private def updateTurnLabel(turn: Turn, gamePhase: GamePhase): Unit = {
    val (playerName, color) = (turn, gamePhase) match {
      case (Turn.FirstPlayer, GamePhase.Positioning)  => ("Player 1 - Position Ships", new Color(0, 200, 0))
      case (Turn.SecondPlayer, GamePhase.Positioning) => ("Player 2 - Position Ships", new Color(0, 0, 200))
      case (Turn.FirstPlayer, GamePhase.Battle)       => ("Player 1's Turn", new Color(0, 200, 0))
      case (Turn.SecondPlayer, GamePhase.Battle)      => ("Player 2's Turn", new Color(0, 0, 200))
      case (_, GamePhase.GameOver)                    => ((s"Game Over! $turn won"), new Color(150, 0, 0))
    }

    turnLabel.text = playerName
    turnLabel.foreground = color
  }

  /** Updates the grid panel with the current game state and handles game over conditions.
    * @param turn the current turn of the game
    */
  def update(turn: Turn): Unit =
    val state = controller.state
    updateTurnLabel(turn, state.gamePhase)
    val buttons = gridManager.createButtons(state, turn)
    gridPanel.contents.clear()
    gridPanel.contents ++= buttons
    gridPanel.revalidate()
    gridPanel.repaint()

    if (state.gamePhase == GamePhase.Battle || state.gamePhase == GamePhase.GameOver) {
      controlPanel.contents -= startButton
      controlPanel.revalidate()
      controlPanel.repaint()

      infoPanel.contents.clear()
      infoPanel.contents += createBattleLegendPanel()
      infoPanel.revalidate()
      infoPanel.repaint()
    }

    GameOverHandler.handleGameOver(this, state)

object GameOverHandler:
  /** Handles the game over condition by displaying a dialog message.
    * @param parent the parent window for the dialog
    * @param state the current game state
    */
  def handleGameOver(parent: Window, state: GameState): Unit =
    if (state.gamePhase == GamePhase.GameOver)
      Dialog.showMessage(
        parent,
        "Game Over! Tutte le navi sono state affondate!",
        title = "Game Finished",
        Dialog.Message.Info
      )
