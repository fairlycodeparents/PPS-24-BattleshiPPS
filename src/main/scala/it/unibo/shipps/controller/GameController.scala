package it.unibo.shipps.controller

import it.unibo.shipps.controller.GamePhase.{Battle, Positioning}
import it.unibo.shipps.logic.BattleLogic
import it.unibo.shipps.model.*
import it.unibo.shipps.view.SimpleGui

import scala.swing.Swing

enum GamePhase:
  case Positioning, Battle, GameOver

case class GameState(
    board: PlayerBoard,
    enemyBoard: PlayerBoard,
    selectedShip: Option[Ship],
    gamePhase: GamePhase,
    attackResult: Map[Position, AttackResult] = Map.empty
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
    copy(attackResult = attackResult + (position -> result))

class GameController(
    initialBoard: PlayerBoard,
    enemyBoard: PlayerBoard,
    val positioning: ShipPositioning,
    var view: SimpleGui
):

  var state: GameState = GameState(initialBoard, enemyBoard, None, Positioning)

  private def handleCellAction(pos: Position)(
      shipAction: (PlayerBoard, Ship, Position) => Either[String, PlayerBoard]
  ): Unit =
    if state.gamePhase == GamePhase.Positioning then
      state = state.selectedShip match
        case None =>
          positioning.getShipAt(state.board, pos) match
            case Right(ship) => state.selectShip(ship)
            case Left(_)     => state
        case Some(ship) =>
          shipAction(state.board, ship, pos) match
            case Right(updatedBoard) =>
              if shipAction == positioning.moveShip then
                state.moveShipTo(updatedBoard)
              else
                state.rotateShipTo(updatedBoard)
            case Left(_) => state
      updateView()

  private def handleBattleClick(pos: Position): Unit =
    val (newState, messages) = BattleLogic.processBattleClick(state, pos)
    state = newState
    messages.foreach(println)
    updateView()

  private def updateView(): Unit =
    val displayBoard = state.gamePhase match
      case GamePhase.Positioning => state.board
      case GamePhase.Battle      => state.enemyBoard
      case GamePhase.GameOver    => state.enemyBoard

    Swing.onEDT(view.update(
      displayBoard,
      if state.gamePhase == GamePhase.Positioning then state.selectedShip else None
    ))

  def onCellClick(pos: Position): Unit = {
    state.gamePhase match
      case GamePhase.Positioning => handleCellAction(pos)(positioning.moveShip)
      case GamePhase.Battle      => handleBattleClick(pos)
      case GamePhase.GameOver    => println("Game is over, no actions allowed")
  }

  def onCellDoubleClick(pos: Position): Unit =
    if state.gamePhase == GamePhase.Positioning then
      handleCellAction(pos) { (board, ship, _) => positioning.rotateShip(board, ship) }

  def onKeyBoardClick(ships: List[Ship]): Unit =
    if state.gamePhase == GamePhase.Positioning then
      positioning.randomPositioning(PlayerBoard(), ships) match
        case Right(newBoard) =>
          state = state.randomizeBoard(newBoard)
          updateView()
        case Left(error) =>
          println("Error randomizing ships")

  def onStartGame(): Unit =
    state.gamePhase match
      case GamePhase.Positioning =>
        state = state.startBattle(positioning.randomPositioning(PlayerBoard(), state.board.ships.toList)
          .getOrElse(PlayerBoard()))
        println("Battle started! Find and sink all enemy ships!")
        updateView()
      case GamePhase.Battle =>
        println("Game already started, cannot start again")
      case GamePhase.GameOver =>
        println("Game over")
