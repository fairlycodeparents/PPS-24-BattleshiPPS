package it.unibo.shipps.controller

import it.unibo.shipps.model.*
import it.unibo.shipps.view.handler.TurnDialogHandler
import it.unibo.shipps.model.player.PlayerFactory.*
import it.unibo.shipps.view.{DifficultySelection, GameView, SetupView}
import it.unibo.shipps.model.player.Player

import scala.swing.*

class GameSetup(val viewFrame: MainFrame):
  private val initialConfig = GameConfig(ShipType.values.map(_ -> 1).toMap)
  private val validators = Seq(
    new MaxOccupancyValidator(maxOccupancy = 0.5),
    new NotEmptyValidator()
  )

  viewFrame.title = "BattleshiPPS: Homepage"
  viewFrame.contents = SetupView.mainPanel
  SetupView.updateConfigDisplay(ConfigurationManager.applyValidators(initialConfig, validators))
  SetupView.setController(this)

  /** Called when any of the ship count spinners change value. */
  def onSpinnerChange(): Unit =
    val currentConfig = SetupView.getGameConfig
    val corrected     = ConfigurationManager.applyValidators(currentConfig, validators)
    SetupView.updateConfigDisplay(corrected)

  /** Handles the event when the "Single Player" button is clicked. */
  def handleSinglePlayerClick(): Unit =
    val options           = Seq("Easy", "Medium", "Hard")
    val choosenDifficulty = new DifficultySelection(options, viewFrame.peer)
    choosenDifficulty.setVisible(true)
    choosenDifficulty.selection match
      case "Easy"   => createController(createBotPlayer(RandomBotAttackStrategy()))
      case "Medium" => createController(createBotPlayer(AverageBotAttackStrategy()))
      case "Hard"   => createController(createBotPlayer(AdvancedBotAttackStrategy()))

  /** Handles the event when the "Multiplayer" button is clicked. */
  def handleMultiPlayerClick(): Unit =
    createController(createHumanPlayer())

  private def showErrorDialog(message: String): Unit =
    Dialog.showMessage(
      viewFrame,
      message,
      "Errore di Configurazione",
      Dialog.Message.Error
    )

  private def createController(secondPlayer: Player): Unit =
    val gameConfig  = SetupView.getGameConfig
    val maybeBoard1 = BoardFactory.createRandomBoard(gameConfig)
    val maybeBoard2 = BoardFactory.createRandomBoard(gameConfig)

    (maybeBoard1, maybeBoard2) match
      case (Right(board1), Right(board2)) =>
        val controller = GameController(
          board1,
          board2,
          createHumanPlayer(),
          secondPlayer
        )
        val view          = new GameView(controller)
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
