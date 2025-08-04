package it.unibo.shipps.controller

import it.unibo.shipps.model._
import it.unibo.shipps.model.Orientation as ShipOrientation
import it.unibo.shipps.view.{SetupView, SimpleGui}

import javax.swing.event.{ChangeEvent, ChangeListener}
import scala.swing.*
import scala.swing.event.ButtonClicked

class GameSetup(val viewFrame: MainFrame):

  private var currentConfig: DefaultConfiguration = DefaultConfiguration(ShipType.values.map(_ -> 0).toMap)

  viewFrame.title = "BattleshiPPS: Homepage"
  viewFrame.contents = SetupView.mainPanel
  SetupView.applyConfig(currentConfig.update)

  for ((ship, spinner) <- SetupView.spinners) do
    spinner.addChangeListener((e: ChangeEvent) =>
      val updatedShips = currentConfig.ships.updated(ship, spinner.getValue.asInstanceOf[Int])
      currentConfig = DefaultConfiguration(updatedShips)
      val corrected = currentConfig.update
      currentConfig = DefaultConfiguration(corrected)
      SetupView.applyConfig(corrected)
    )

  viewFrame.listenTo(SetupView.singlePlayerButton, SetupView.multiPlayerButton)
  viewFrame.reactions += {
    case ButtonClicked(SetupView.singlePlayerButton) =>
      createController(BotPlayer(RandomBotAttackStrategy()))
    case ButtonClicked(SetupView.multiPlayerButton) =>
      createController(HumanPlayer())
  }

  private def createBoard(): PlayerBoard =
    val ships = ShipType.values.toSeq.flatMap(shipType =>
      val num = SetupView.spinners(shipType).getValue.asInstanceOf[Int]
      Seq.fill(num)(shipType.at(Position(0, 0), ShipOrientation.Horizontal))
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
    viewFrame.close()
