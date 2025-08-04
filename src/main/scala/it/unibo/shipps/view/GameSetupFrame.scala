package it.unibo.shipps.view

import it.unibo.shipps.controller.{GameController, Turn}
import it.unibo.shipps.model.*

import javax.swing.{JSpinner, SpinnerNumberModel}
import scala.swing.*
import scala.swing.event.*

object GameSetupFrame:

  private val maxShipCount    = 5
  private val minShipCount    = 0
  private val stepShipCount   = 1
  private val framePadding    = 30
  private val verticalSpacing = 10

  def top: MainFrame = new MainFrame:
    title = "BattleshiPPS: Homepage"

    val singlePlayerButton = new Button("Play vs Computer")
    val multiPlayerButton  = new Button("Multiplayer")

    var currentConfig: DefaultConfiguration = DefaultConfiguration(ShipType.values.map(_ -> 0).toMap)

    val spinners: Map[ShipType, JSpinner] = ShipType.values.map { ship =>
      ship -> new JSpinner(new SpinnerNumberModel(minShipCount, minShipCount, maxShipCount, stepShipCount))
    }.toMap

    def applyConfig(config: Map[ShipType, Int]): Unit =
      for (ship, spinner) <- spinners do
        val value = config.getOrElse(ship, 0)
        if spinner.getValue != value then spinner.setValue(value)

    for (ship, spinner) <- spinners do
      spinner.addChangeListener(_ =>
        val updatedShips = currentConfig.ships.updated(ship, spinner.getValue.asInstanceOf[Int])
        currentConfig = DefaultConfiguration(updatedShips)
        val corrected = currentConfig.update
        currentConfig = DefaultConfiguration(corrected)
        applyConfig(corrected)
      )

    val shipsSettingsPanel: GridPanel = new GridPanel(ShipType.values.length, 2):
      for shipType <- ShipType.values do
        contents += new Label(shipType.toString)
        contents += Component.wrap(spinners(shipType))

    val buttonPanel: FlowPanel = new FlowPanel:
      contents += singlePlayerButton
      contents += multiPlayerButton

    contents = new BoxPanel(scala.swing.Orientation.Vertical):
      contents += shipsSettingsPanel
      contents += Swing.VStrut(verticalSpacing)
      contents += buttonPanel
      border = Swing.EmptyBorder(framePadding)

    private def createBoard(): PlayerBoard =
      val ships = ShipType.values.toSeq.flatMap(shipType =>
        val num = spinners(shipType).getValue.asInstanceOf[Int]
        Seq.fill(num)(shipType.at(Position(0, 0), it.unibo.shipps.model.Orientation.Horizontal))
      )
        .toList
      ShipPositioningImpl
        .randomPositioning(PlayerBoard(), ships)
        .getOrElse(throw new IllegalStateException("Invalid ship positioning"))

    private def createController(secondPlayer: Player): Unit =
      val controller = GameController(
        createBoard(),
        createBoard(),
        HumanPlayer(),
        secondPlayer,
        ShipPositioningImpl,
        null
      )
      val view = new SimpleGui(controller)
      controller.view = view
      view.update(Turn.FirstPlayer)
      view.visible = true
      this.close()

    listenTo(singlePlayerButton, multiPlayerButton)
    reactions += {
      case ButtonClicked(`singlePlayerButton`) =>
        createController(BotPlayer(RandomBotAttackStrategy()))
      case ButtonClicked(`multiPlayerButton`) =>
        createController(HumanPlayer())
    }
