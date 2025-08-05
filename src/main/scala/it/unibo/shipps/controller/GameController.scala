package it.unibo.shipps.controller

import it.unibo.shipps.controller.GamePhase.{Battle, Positioning}
import it.unibo.shipps.controller.GameStateManager.DialogAction
import it.unibo.shipps.controller.Turn.FirstPlayer
import it.unibo.shipps.model.*
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.components.DialogFactory
import it.unibo.shipps.view.renderer.ColorScheme

import java.awt.BorderLayout
import javax.swing.{JDialog, JLabel, Timer}
import scala.swing.{Font, Swing}

/** Represents the different phases of the game.
  * Positioning: Players place their ships on the board.
  * Battle: Players take turns attacking each other's boards.
  * GameOver: The game has ended, either by sinking all enemy ships or losing all own ships.
  */
enum GamePhase:
  case Positioning, Battle, GameOver

/** Represents the turn of the player in the game.
  * FirstPlayer: The first player is taking their turn.
  * SecondPlayer: The second player is taking their turn.
  */
enum Turn:
  case FirstPlayer, SecondPlayer

/** Represents the state of the game
  * @param board the first player's board
  * @param enemyBoard the second player's board
  * @param selectedShip the ship currently selected by the player
  * @param gamePhase the current phase of the game
  * @param attackResult a map of first player positions to their attack results
  * @param cellColors a map of first player positions to their display colors on the first player's board
  * @param enemyAttackResult a map of enemy positions to their attack results
  * @param enemyCellColors a map of enemy positions to their display colors on the second player's board
  */
case class GameState(
    board: PlayerBoard,
    enemyBoard: PlayerBoard,
    selectedShip: Option[Ship],
    gamePhase: GamePhase,
    attackResult: Map[Position, AttackResult] = Map.empty,
    cellColors: Map[Position, java.awt.Color] = Map.empty,
    enemyAttackResult: Map[Position, AttackResult] = Map.empty,
    enemyCellColors: Map[Position, java.awt.Color] = Map.empty
):
  /** Selects a ship for the player to move or rotate during the positioning phase.
    * @param ship the ship to select
    * @return updated GameState with the selected ship
    */
  def selectShip(ship: Ship): GameState =
    copy(selectedShip = Some(ship))

  /** Starts the battle phase of the game with a new enemy board.
    * @param newEnemyBoard the board of the enemy player
    * @return updated GameState with the battle phase started
    */
  def startBattle(newEnemyBoard: PlayerBoard): GameState =
    copy(
      gamePhase = GamePhase.Battle,
      enemyBoard = newEnemyBoard,
      attackResult = Map.empty
    )

  /** Adds the result of an attack to the game state.
    * @param position the position of the attack on the second player's board
    * @param result the result of the attack of the first player on the second player's board
    * @return updated GameState with the attack result added
    */
  def addAttackResult(position: Position, result: AttackResult): GameState =
    result match
      case AttackResult.AlreadyAttacked =>
        this
      case AttackResult.Miss =>
        copy(
          attackResult = attackResult + (position -> result),
          cellColors = cellColors + (position     -> ColorScheme.MISS)
        )
      case AttackResult.Hit(_) =>
        copy(
          attackResult = attackResult + (position -> result),
          cellColors = cellColors + (position     -> ColorScheme.HIT)
        )
      case AttackResult.Sunk(_) | AttackResult.EndOfGame(_) =>
        copy(
          attackResult = attackResult + (position -> result),
          cellColors = cellColors + (position     -> ColorScheme.SUNK)
        )

  /** Adds the result of an enemy attack to the game state.
    * @param position the position of the attack on the first player's board
    * @param result the result of the attack of the second player on the first player's board
    * @return updated GameState with the enemy attack result added
    */
  def addEnemyAttackResult(position: Position, result: AttackResult): GameState = {
    result match
      case AttackResult.AlreadyAttacked =>
        this
      case AttackResult.Miss =>
        copy(
          enemyAttackResult = enemyAttackResult + (position -> result),
          enemyCellColors = enemyCellColors + (position     -> ColorScheme.MISS)
        )
      case AttackResult.Hit(_) =>
        copy(
          enemyAttackResult = enemyAttackResult + (position -> result),
          enemyCellColors = enemyCellColors + (position     -> ColorScheme.HIT)
        )
      case AttackResult.Sunk(_) | AttackResult.EndOfGame(_) =>
        copy(
          enemyAttackResult = enemyAttackResult + (position -> result),
          enemyCellColors = enemyCellColors + (position     -> ColorScheme.SUNK)
        )
  }

/** Represents the controller for the game, managing the game state and interactions.
  * @param initialBoard the board of the first player
  * @param enemyBoard the board of the second player
  * @param firstPlayer the first player in the game
  * @param secondPlayer the second player in the game
  */
class GameController(
    initialBoard: PlayerBoard,
    enemyBoard: PlayerBoard,
    firstPlayer: Player,
    secondPlayer: Player
):

  var state: GameState        = GameState(initialBoard, enemyBoard, None, Positioning)
  var view: Option[SimpleGui] = None

  private var turn: Turn                          = Turn.FirstPlayer
  private var currentDialog: Option[JDialog]      = None
  private var isPositioningPhaseComplete: Boolean = false
  private val positioning: ShipPositioning        = ShipPositioningImpl

  /** Handle the bot turn. */
  private def executeBotTurn(): Unit = {
    val result = GameStateManager.handleBotTurn(state, turn, firstPlayer, secondPlayer)
    applyGameActionResult(result)
  }

  /** Set a delay before executing an action.
    * @param action the action to execute after the delay
    * @param delayMs the delay in milliseconds before executing the action
    */
  private def executeWithDelay(action: () => Unit, delayMs: Int = 2000): Unit = {
    val timer = new Timer(delayMs, _ => action())
    timer.setRepeats(false)
    timer.start()
  }

  /** Initializes the game controller with the view.
    * @param turn the turn of the game
    */
  private def updateView(turn: Turn): Unit =
    Swing.onEDT(view.get.update(turn))

  /** Show the dialog indicating whose turn it is.
    * @param playerName the name of the player whose turn it is
    */
  private def showTurnDialog(playerName: String): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createTurnDialog(view.get, playerName)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  /** Shows a waiting dialog while the bot is taking its turn. */
  private def showWaitingDialog(): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createWaitingDialog(view.get)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  /** Hides the current dialog. */
  private def hideCurrentDialog(): Unit =
    DialogFactory.hideDialogOpt(currentDialog)
    currentDialog = None

  /** Sets the view for the game controller.
    * @param result the view to set
    */
  private def applyGameActionResult(result: GameStateManager.GameActionResult): Unit =
    state = result.newState
    turn = result.newTurn

    result.messages.foreach(println)

    result.showDialog.foreach(handleDialogAction)

    if BattleController.isBotTurn(turn, firstPlayer, secondPlayer) &&
      state.gamePhase == GamePhase.Battle
    then
      executeWithDelay(() => executeBotTurn())

    updateView(turn)

  /** Handles the dialog actions based on the current game state.
    * @param action the dialog action to handle
    */
  private def handleDialogAction(action: DialogAction): Unit =
    action match
      case DialogAction.ShowTurnDialog(playerName) =>
        showTurnDialog(playerName)
      case DialogAction.ShowWaitingDialog =>
        showWaitingDialog()
      case DialogAction.HideDialog =>
        hideCurrentDialog()

  /** Handles the click on a cell based on the current game phase.
    * @param pos the position of the cell clicked
    */
  def onCellClick(pos: Position): Unit =
    val result = state.gamePhase match
      case GamePhase.Positioning =>
        GameStateManager.handlePositioningClick(state, pos, turn, positioning)
      case GamePhase.Battle =>
        GameStateManager.handleBattleClick(state, pos, turn, firstPlayer, secondPlayer)
      case GamePhase.GameOver =>
        GameStateManager.GameActionResult(state, turn, List("Game is over, no actions allowed"))

    applyGameActionResult(result)

  /** Handles the double click on a cell to rotate the ship during the positioning phase.
    * @param pos the position of the cell double-clicked
    */
  def onCellDoubleClick(pos: Position): Unit =
    if state.gamePhase == GamePhase.Positioning then
      val result = GameStateManager.handlePositioningDoubleClick(state, pos, turn, positioning)
      applyGameActionResult(result)

  /** Handles the keyboard click to randomize ship positioning during the positioning phase.
    * @param ships the list of ships to randomize
    */
  def onKeyBoardClick(ships: List[Ship]): Unit =
    if state.gamePhase == GamePhase.Positioning then
      val result = GameStateManager.handleRandomizePositions(state, ships, turn, positioning)
      applyGameActionResult(result)

  /** Handles the action when the game starts. */
  def onStartGame(): Unit = {
    val result = GameStateManager.handleStartGame(
      state,
      turn,
      firstPlayer,
      secondPlayer,
      positioning,
      isPositioningPhaseComplete
    )

    if !isPositioningPhaseComplete && turn == Turn.FirstPlayer && !secondPlayer.isABot then
      isPositioningPhaseComplete = true

    applyGameActionResult(result)
  }
