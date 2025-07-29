package it.unibo.shipps.view

import it.unibo.shipps.model.ShipType
import scala.swing.*
import scala.swing.event.*
import javax.swing.{JSpinner, SpinnerNumberModel}

object HomeFrame:

  private val maxShipCount    = 5
  private val minShipCount    = 0
  private val stepShipCount   = 1
  private val framePadding    = 30
  private val verticalSpacing = 10

  def top: MainFrame = new MainFrame:
    title = "BattleshiPPS: Homepage"

    val singlePlayerButton = new Button("Play vs Computer")
    val multiPlayerButton  = new Button("Multiplayer")

    val spinners: Map[ShipType, JSpinner] = ShipType.values.map(ship =>
      ship -> new JSpinner(new SpinnerNumberModel(minShipCount, minShipCount, maxShipCount, stepShipCount))
    ).toMap

    val shipsSettingsPanel: GridPanel = new GridPanel(ShipType.values.length, 2):
      for shipType <- ShipType.values do
        contents += new Label(shipType.toString)
        contents += Component.wrap(spinners(shipType))

    val buttonPanel: FlowPanel = new FlowPanel:
      contents += singlePlayerButton
      contents += multiPlayerButton

    contents = new BoxPanel(Orientation.Vertical):
      contents += shipsSettingsPanel
      contents += Swing.VStrut(verticalSpacing)
      contents += buttonPanel
      border = Swing.EmptyBorder(framePadding)

    listenTo(singlePlayerButton, multiPlayerButton)
    reactions += {
      case ButtonClicked(`singlePlayerButton`) => println("Start a SinglePlayer game")
      case ButtonClicked(`multiPlayerButton`)  => println("Start a MultiPlayer game")
    }

@main def run(): Unit =
  Swing.onEDT {
    HomeFrame.top.visible = true
  }
