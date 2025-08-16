package it.unibo.shipps.view

import it.unibo.shipps.model.ship.ShipType

import javax.swing.{JDialog, JSpinner, SpinnerNumberModel}
import scala.swing.*

class DifficultySelection(options: Seq[String], owner: java.awt.Frame)
    extends JDialog(owner, "Choose the difficulty", true):

  private val comboBox  = new ComboBox(options)
  private val okButton  = new Button("OK")
  var selection: String = options.head

  okButton.action = Action("OK"):
    selection = comboBox.selection.item
    dispose()

  val mainPanel: BoxPanel = new BoxPanel(Orientation.Vertical):
    border = Swing.EmptyBorder(10)
    contents += new Label("Choose an option:")
    contents += comboBox
    contents += new FlowPanel:
      contents += okButton

  setContentPane(mainPanel.peer)
  pack()
  setLocationRelativeTo(owner)

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
