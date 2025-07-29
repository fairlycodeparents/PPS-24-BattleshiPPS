package it.unibo.shipps.view

import it.unibo.shipps.model.ShipType

import scala.swing.*
import scala.swing.event.*
import javax.swing.{JSpinner, SpinnerNumberModel}

object HomeFrame:
  def top: MainFrame = new MainFrame:
    title = "BattleshiPPS: Homepage"

    val singlePlayerButton = new Button("Play vs Computer")
    val multiPlayerButton  = new Button("Multiplayer")
    val spinners: Map[ShipType, JSpinner] = ShipType.values.map(ship =>
      ship -> new JSpinner(new SpinnerNumberModel(0, 0, 5, 1))
    ).toMap

    val shipsSettingsPanel: GridPanel = new GridPanel(ShipType.values.length, 2):
      for (shipType <- ShipType.values) {
        contents += new Label(shipType.toString)
        contents += Component.wrap(spinners(shipType))
      }

    val buttonPanel: FlowPanel = new FlowPanel:
      contents += singlePlayerButton
      contents += multiPlayerButton

    contents = new BoxPanel(Orientation.Vertical):
      contents += shipsSettingsPanel
      contents += Swing.VStrut(10)
      contents += buttonPanel
      border = Swing.EmptyBorder(30, 30, 10, 30)

    listenTo(singlePlayerButton, multiPlayerButton)
    reactions += {
      case ButtonClicked(`singlePlayerButton`) => println("Start a SinglePlayer game")
      case ButtonClicked(`multiPlayerButton`)  => println("Start a MultiPlayer game")
    }

@main def run(): Unit =
  Swing.onEDT {
    HomeFrame.top.visible = true
  }
