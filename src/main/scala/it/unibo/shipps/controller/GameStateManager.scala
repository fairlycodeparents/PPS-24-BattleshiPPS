package it.unibo.shipps.controller

import it.unibo.shipps.model.*
import it.unibo.shipps.model.player.Player
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.TurnLogic
import it.unibo.shipps.view.GameView
import it.unibo.shipps.view.components.DialogFactory
import it.unibo.shipps.view.components.DialogFactory.createBotResultDialog

import scala.util.Try

/** Manages game state transitions and game flow logic */
object GameStateManager:

  /** Represents different dialog actions */
  enum DialogAction:
    case ShowTurnDialog(playerName: String)
    case ShowWaitingDialog
    case RetryAttack
    case HideDialog
    case ShowBotResultDialog(result: String)

  /** Result of a game action */
  case class GameActionResult(
      newState: GameState,
      newTurn: Turn,
      messages: List[String],
      showDialog: Option[DialogAction] = None
  ):
    /** Adds a message to the action result
      * @param dialog dialog action to show
      * @return new GameActionResult with updated dialog
      */
    def withDialog(dialog: DialogAction): GameActionResult =
      copy(showDialog = Some(dialog))

  /** Handles bot game start
    * @param gameState   current game state
    * @param positioning ship positioning logic
    * @return game action result
    */
  private def handleBotGameStart(gameState: GameState, positioning: ShipPositioning): GameActionResult =
    positioning.randomPositioning(PlayerBoard(), gameState.board.ships.toList) match
      case Right(enemyBoard) =>
        val newState = gameState.copy(enemyBoard = enemyBoard).startBattle(enemyBoard)
        GameActionResult(newState, Turn.FirstPlayer, List("Battle started! Find and sink all enemy ships!"))
      case Left(error) =>
        GameActionResult(gameState, Turn.FirstPlayer, List(s"Error positioning bot ships: $error"))

  /** Handles two-player positioning
    * @param gameState current game state
    * @return game action result
    */
  private def handleTwoPlayerPositioningStart(gameState: GameState): GameActionResult =
    GameActionResult(
      gameState,
      Turn.SecondPlayer,
      List("Player 2: Position your ships and press Start Game again")
    ).withDialog(DialogAction.ShowTurnDialog("Player 2 - Position your ships"))

  /** Handles two-player battle start
    * @param gameState current game state
    * @return game action result
    */
  private def handleTwoPlayerBattleStart(gameState: GameState): GameActionResult =
    val newState = gameState.startBattle(gameState.enemyBoard)
    GameActionResult(
      newState,
      Turn.FirstPlayer,
      List("Battle started! Player 1 attacks first!")
    ).withDialog(DialogAction.ShowTurnDialog("Player 1 - Battle begins!"))

  /** Determines the dialog action based on the current turn and players
    * @param turn         current turn
    * @param firstPlayer  first player
    * @param secondPlayer second player
    * @return dialog action to show
    */
  private def determineDialogAction(turn: Turn, firstPlayer: Player, secondPlayer: Player): DialogAction =
    if TurnLogic.isBotTurn(turn, firstPlayer, secondPlayer) then
      DialogAction.ShowWaitingDialog
    else
      val playerName = turn match
        case Turn.FirstPlayer  => "Player 1"
        case Turn.SecondPlayer => "Player 2"
      DialogAction.ShowTurnDialog(playerName)

  /** Handles start game logic
    * @param gameState current game state
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @param positioning ship positioning logic
    * @param isPositioningComplete whether positioning phase is complete
    * @return game action result
    */
  def handleStartGame(
      gameState: GameState,
      turn: Turn,
      firstPlayer: Player,
      secondPlayer: Player,
      positioning: ShipPositioning,
      isPositioningComplete: Boolean
  ): GameActionResult =
    gameState.gamePhase match
      case GamePhase.Positioning =>
        if secondPlayer.isABot then
          handleBotGameStart(gameState, positioning)
        else if !isPositioningComplete then
          handleTwoPlayerPositioningStart(gameState)
        else
          handleTwoPlayerBattleStart(gameState)
      case GamePhase.Battle =>
        GameActionResult(gameState, turn, List("Game already started"))
      case GamePhase.GameOver =>
        GameActionResult(gameState, turn, List("Game over"))

  /** Handles positioning click
    * @param gameState current game state
    * @param position clicked position
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return game action result
    */
  def handlePositioningClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handlePositioningClick(gameState, position, turn, positioning)
    GameActionResult(newState, turn, List())

  /** Handles positioning double click
    * @param gameState current game state
    * @param position clicked position
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return game action result
    */
  def handlePositioningDoubleClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handlePositioningDoubleClick(gameState, position, turn, positioning)
    GameActionResult(newState, turn, List())

  /** Handles randomize positions
    * @param gameState current game state
    * @param ships list of ships
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return game action result
    */
  def handleRandomizePositions(
      gameState: GameState,
      ships: List[Ship],
      turn: Turn,
      positioning: ShipPositioning
  ): GameActionResult =
    val newState = PositioningHandler.handleRandomizePositions(gameState, ships, turn, positioning)
    GameActionResult(newState, turn, List())

  /** Handles battle click
    * @param gameState current game state
    * @param position clicked position
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @return game action result
    */
  def handleBattleClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      firstPlayer: Player,
      secondPlayer: Player
  ): GameActionResult =
    val currentPlayer = TurnLogic.getCurrentPlayer(turn, firstPlayer, secondPlayer)
    val battleResult  = BattleHandler.processHumanAttack(gameState, currentPlayer, turn, position)

    if battleResult.gameOver then
      GameActionResult(battleResult.newState, turn, battleResult.messages)
    else
      val clickResult = BattleLogic.processBattleClick(gameState, currentPlayer, turn, Some(position))
      if clickResult.shouldChangeTurn then
        val newTurn      = TurnLogic.switchTurn(turn)
        val dialogAction = determineDialogAction(newTurn, firstPlayer, secondPlayer)
        GameActionResult(battleResult.newState, newTurn, battleResult.messages, Some(dialogAction))
      else
        GameActionResult(battleResult.newState, turn, battleResult.messages, Some(DialogAction.RetryAttack))

  /** Handles bot turn execution
    * @param gameState current game state
    * @param turn current turn
    * @param firstPlayer first player
    * @param secondPlayer second player
    * @return game action result
    */
  def handleBotTurn(
      gameState: GameState,
      turn: Turn,
      firstPlayer: Player,
      secondPlayer: Player
  ): GameActionResult =
    val currentPlayer = TurnLogic.getCurrentPlayer(turn, firstPlayer, secondPlayer)
    val battleResult  = BattleHandler.processBotAttack(gameState, currentPlayer, turn)

    if battleResult.gameOver then
      GameActionResult(battleResult.newState, turn, battleResult.messages)
        .withDialog(DialogAction.HideDialog)
    else
      val newTurn = TurnLogic.switchTurn(turn)
      val playerName = newTurn match
        case Turn.FirstPlayer  => "Player 1"
        case Turn.SecondPlayer => "Player 2"
      GameActionResult(battleResult.newState, newTurn, battleResult.messages)
        .withDialog(DialogAction.ShowTurnDialog(playerName))
