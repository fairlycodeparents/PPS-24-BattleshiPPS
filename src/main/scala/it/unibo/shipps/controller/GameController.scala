package it.unibo.shipps.controller

import it.unibo.shipps.controller.GamePhase.{Battle, Positioning}
import it.unibo.shipps.model.*
import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.renderer.ColorScheme

import scala.swing.Swing

/** Represents the different phases of the game.
  * Positioning: Players place their ships on the board.
  * Battle: Players take turns attacking each other's boards.
  * GameOver: The game has ended, either by sinking all enemy ships or losing all own ships.
  */
enum GamePhase:
  case Positioning, Battle, GameOver

/** Represents the state of the game
  * @param board the player's board
  * @param enemyBoard the enemy's board
  * @param selectedShip the ship currently selected by the player
  * @param gamePhase the current phase of the game
  * @param attackResult a map of positions to their attack results
  * @param cellColors a map of positions to their display colors
  */
case class GameState(
    board: PlayerBoard,
    enemyBoard: PlayerBoard,
    selectedShip: Option[Ship],
    gamePhase: GamePhase,
    attackResult: Map[Position, AttackResult] = Map.empty,
    cellColors: Map[Position, java.awt.Color] = Map.empty
):
  def selectShip(ship: Ship): GameState =
    copy(selectedShip = Some(ship))

  def randomizeBoard(newBoard: PlayerBoard): GameState =
    copy(board = newBoard, selectedShip = None)

  def moveShipTo(newBoard: PlayerBoard): GameState =
    copy(board = newBoard, selectedShip = None)

  def rotateShipTo(newBoard: PlayerBoard): GameState =
    copy(board = newBoard, selectedShip = None)

  def startBattle(newEnemyBoard: PlayerBoard): GameState =
    copy(
      gamePhase = GamePhase.Battle,
      enemyBoard = newEnemyBoard,
      attackResult = Map.empty
    )

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

/** Represents the controller for the game, managing the game state and interactions.
  * @param initialBoard the board of the player
  * @param enemyBoard the board of the enemy
  * @param positioning the ship positioning logic
  * @param view the GUI view for displaying the game state
  */
class GameController(
    initialBoard: PlayerBoard,
    enemyBoard: PlayerBoard,
    val positioning: ShipPositioning,
    var view: SimpleGui
):

  var state: GameState = GameState(initialBoard, enemyBoard, None, Positioning)

  private def handleCellAction(currentState: GameState, pos: Position)(
      shipAction: (PlayerBoard, Ship, Position) => Either[String, PlayerBoard]
  ): GameState =
    if state.gamePhase == GamePhase.Positioning then
      currentState.selectedShip match
        case None =>
          positioning.getShipAt(currentState.board, pos) match
            case Right(ship) => currentState.selectShip(ship)
            case Left(_)     => currentState
        case Some(ship) =>
          shipAction(currentState.board, ship, pos) match
            case Right(updatedBoard) =>
              if shipAction == positioning.moveShip then
                currentState.moveShipTo(updatedBoard)
              else
                currentState.rotateShipTo(updatedBoard)
            case Left(_) => currentState
    else currentState

  private def handleBattleClick(currentState: GameState, pos: Position): (GameState, List[String]) =
    BattleLogic.processBattleClick(currentState, pos)

  private def updateView(): Unit =
    val displayBoard = state.gamePhase match
      case GamePhase.Positioning => state.board
      case GamePhase.Battle      => state.enemyBoard
      case GamePhase.GameOver    => state.enemyBoard

    Swing.onEDT(view.update(
      displayBoard,
      if state.gamePhase == GamePhase.Positioning then state.selectedShip else None
    ))

  /** Handles the click on a cell based on the current game phase.
    * @param pos the position of the cell clicked
    */
  def onCellClick(pos: Position): Unit = {
    val newState = state.gamePhase match
      case GamePhase.Positioning => handleCellAction(state, pos)(positioning.moveShip)
      case GamePhase.Battle =>
        val (updatedState, messages) = handleBattleClick(state, pos)
        messages.foreach(println)
        updatedState
      case GamePhase.GameOver =>
        println("Game is over, no actions allowed")
        state

    state = newState
    updateView()
  }

  /** Handles the double click on a cell to rotate the ship during the positioning phase.
    * @param pos the position of the cell double-clicked
    */
  def onCellDoubleClick(pos: Position): Unit =
    if state.gamePhase == GamePhase.Positioning then
      val newState = handleCellAction(state, pos) { (board, ship, _) => positioning.rotateShip(board, ship) }
      state = newState
      updateView()

  /** Handles the keyboard click to randomize ship positioning during the positioning phase.
    * @param ships the list of ships to randomize
    */
  def onKeyBoardClick(ships: List[Ship]): Unit =
    if state.gamePhase == GamePhase.Positioning then
      positioning.randomPositioning(PlayerBoard(), ships) match
        case Right(newBoard) =>
          state = state.randomizeBoard(newBoard)
          updateView()
        case Left(error) =>
          println("Error randomizing ships")

  /** Handles the action when the game starts. */
  def onStartGame(): Unit = {
    val (newState, message) = state.gamePhase match
      case GamePhase.Positioning =>
        val enemyBoard = positioning.randomPositioning(PlayerBoard(), state.board.ships.toList)
          .getOrElse(PlayerBoard())
        (state.startBattle(enemyBoard), "Battle started! Find and sink all enemy ships!")
      case GamePhase.Battle =>
        (state, println("Game already started, cannot start again"))
      case GamePhase.GameOver =>
        (state, println("Game over"))

    state = newState
    println(message)
    updateView()
  }
