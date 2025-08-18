package it.unibo.shipps.controller

import it.unibo.shipps.model.*
import it.unibo.shipps.view.handler.TurnDialogHandler
import it.unibo.shipps.model.player.{BotPlayer, HumanPlayer, Player}
import it.unibo.shipps.model.ship.ShipType
import it.unibo.shipps.model.ship.ShipType.*
import it.unibo.shipps.view.{DifficultySelection, SetupView, SimpleGui}

import javax.swing.event.ChangeEvent
import scala.swing.*
import scala.swing.event.ButtonClicked

class GameSetup(val viewFrame: MainFrame):
  private var currentConfig: GameConfig = GameConfig(ShipType.values.map(_ -> 1).toMap)
  private val validators = Seq(
    new MaxOccupancyValidator(maxOccupancy = 0.5),
    new NotEmptyValidator()
  )

  viewFrame.title = "BattleshiPPS: Homepage"
  viewFrame.contents = SetupView.mainPanel
  SetupView.applyConfig(ConfigurationManager.applyValidators(currentConfig, validators).ships)

  private def updateConfig(shipType: ShipType, count: Int): Unit =
    val updatedShips = currentConfig.ships.updated(shipType, count)
    currentConfig = GameConfig(updatedShips)
    val corrected = ConfigurationManager.applyValidators(currentConfig, validators)
    currentConfig = corrected
    SetupView.applyConfig(corrected.ships)

  for ((ship, spinner) <- SetupView.spinners) do
    spinner.addChangeListener((e: ChangeEvent) =>
      updateConfig(ship, spinner.getValue.asInstanceOf[Int])
    )

  viewFrame.listenTo(SetupView.singlePlayerButton, SetupView.multiPlayerButton)
  viewFrame.reactions += {
    case ButtonClicked(SetupView.singlePlayerButton) =>
      val options           = Seq("Easy", "Medium", "Hard")
      val choosenDifficulty = new DifficultySelection(options, viewFrame.peer)
      choosenDifficulty.setVisible(true)
      choosenDifficulty.selection match
        case "Easy"   => createController(BotPlayer(RandomBotAttackStrategy()))
        case "Medium" => createController(BotPlayer(AverageBotAttackStrategy()))
        case "Hard"   => createController(BotPlayer(AdvancedBotAttackStrategy()))
    case ButtonClicked(SetupView.multiPlayerButton) =>
      createController(HumanPlayer())
  }

  private def showErrorDialog(message: String): Unit =
    Dialog.showMessage(
      viewFrame,
      message,
      "Errore di Configurazione",
      Dialog.Message.Error
    )

  private def createController(secondPlayer: Player): Unit =
    val maybeBoard1 = BoardFactory.createRandomBoard(currentConfig)
    val maybeBoard2 = BoardFactory.createRandomBoard(currentConfig)

    (maybeBoard1, maybeBoard2) match
      case (Right(board1), Right(board2)) =>
        val controller = GameController(
          board1,
          board2,
          HumanPlayer(),
          secondPlayer
        )
        val view          = new SimpleGui(controller)
        val dialogHandler = new TurnDialogHandler(view)
        controller.view = Some(view)
        controller.dialogHandler = Some(dialogHandler)
        view.update(Turn.FirstPlayer)
        view.visible = true
        viewFrame.close()
      case (Left(error), _) =>
        showErrorDialog(error)
      case (_, Left(error)) =>
        showErrorDialog(error)
