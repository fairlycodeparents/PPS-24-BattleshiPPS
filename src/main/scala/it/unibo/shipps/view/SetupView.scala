package it.unibo.shipps.view

import it.unibo.shipps.model.ship.ShipType
import javax.swing.{JSpinner, SpinnerNumberModel}
import scala.swing.*

object SetupView:

  val singlePlayerButton = new Button("Play vs Computer")
  val multiPlayerButton  = new Button("Multiplayer")

  private val maxShipCount    = 5
  private val minShipCount    = 0
  private val stepShipCount   = 1
  private val framePadding    = 30
  private val verticalSpacing = 10

  val spinners: Map[ShipType, JSpinner] = ShipType.values.map(ship =>
    ship -> new JSpinner(new SpinnerNumberModel(minShipCount, minShipCount, maxShipCount, stepShipCount))
  ).toMap

  private val shipsSettingsPanel: GridPanel = new GridPanel(ShipType.values.length, 2):
    for (shipType, spinner) <- spinners do
      contents += new Label(shipType.toString)
      contents += Component.wrap(spinner)

  private val buttonPanel: FlowPanel = new FlowPanel:
    contents += singlePlayerButton
    contents += multiPlayerButton

  val mainPanel: BoxPanel = new BoxPanel(scala.swing.Orientation.Vertical):
    contents += shipsSettingsPanel
    contents += Swing.VStrut(verticalSpacing)
    contents += buttonPanel
    border = Swing.EmptyBorder(framePadding)

  def applyConfig(config: Map[ShipType, Int]): Unit =
    for ((ship, spinner) <- spinners) do
      val value = config.getOrElse(ship, 0)
      if (spinner.getValue != value) spinner.setValue(value)
