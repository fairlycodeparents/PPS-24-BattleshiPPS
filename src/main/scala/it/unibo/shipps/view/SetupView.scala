package it.unibo.shipps.view

import it.unibo.shipps.model.{GameConfig, ShipType}
import javax.swing.{JDialog, JSpinner, SpinnerNumberModel}
import scala.swing.*
import it.unibo.shipps.controller.GameSetup
import scala.swing.event.ButtonClicked

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
  private var controller: Option[GameSetup] = None

  /** Sets the controller for handling user interactions.
    * @param c The GameSetup controller instance.
    */
  def setController(c: GameSetup): Unit =
    controller = Some(c)
    mainPanel.listenTo(singlePlayerButton, multiPlayerButton)
    mainPanel.reactions += {
      case ButtonClicked(`singlePlayerButton`) =>
        controller.foreach(_.handleSinglePlayerClick())
      case ButtonClicked(`multiPlayerButton`) =>
        controller.foreach(_.handleMultiPlayerClick())
    }

  /** Retrieves the current game configuration based on user selections.
    * @return The current GameConfig instance.
    */
  def getGameConfig: GameConfig =
    GameConfig(spinners.map { case (shipType, spinner) =>
      shipType -> spinner.getValue.asInstanceOf[Int]
    })

  private val singlePlayerButton = new Button("Play vs Computer")
  private val multiPlayerButton  = new Button("Multiplayer")

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

  def updateConfigDisplay(config: GameConfig): Unit =
    for ((ship, spinner) <- spinners) do
      val value = config.ships.getOrElse(ship, 0)
      if (spinner.getValue != value) spinner.setValue(value)
