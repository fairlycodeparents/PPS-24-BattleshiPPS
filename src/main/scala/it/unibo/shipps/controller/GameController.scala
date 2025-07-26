package it.unibo.shipps.controller

import it.unibo.shipps.controller.GamePhase.{Battle, Positioning}
import it.unibo.shipps.model.*
import it.unibo.shipps.view.SimpleGui

import scala.swing.Swing

enum GamePhase:
  case Positioning, Battle, GameOver

case class GameState(
                      board: PlayerBoard,
                      enemyBoard: PlayerBoard,
                      shipAttack: ShipAttack,
                      selectedShip: Option[Ship],
                      gamePhase: GamePhase
                    )

class GameController(
    initialBoard: PlayerBoard,
    enemyBoard: PlayerBoard,
    val positioning: ShipPositioning,
    var view: SimpleGui
):

  var state: GameState = GameState(initialBoard, enemyBoard, ShipAttack(enemyBoard, Set.empty), None, Positioning)

  private def handleCellAction(pos: Position)(
      shipAction: (PlayerBoard, Ship, Position) => Either[String, PlayerBoard]
  ): Unit =
    if state.gamePhase == GamePhase.Positioning then
      state = state.selectedShip match
        case None =>
          positioning.getShipAt(state.board, pos) match
            case Right(ship) => state.copy(selectedShip = Some(ship))
            case Left(_)     => state
        case Some(ship) =>
          shipAction(state.board, ship, pos) match
            case Right(updatedBoard) => GameState(updatedBoard, enemyBoard, ShipAttack(enemyBoard, Set.empty), None, Positioning)
            case Left(_)             => state
      updateView()

  private def handleKeyboardClick(ships: List[Ship], board: PlayerBoard): Either[String, GameState] =
    positioning.randomPositioning(PlayerBoard(), ships).map(newBoard => GameState(newBoard, enemyBoard, ShipAttack(enemyBoard, Set.empty), None, Positioning))

  private def updateView(): Unit =
    val displayBoard = state.gamePhase match
      case GamePhase.Positioning  => state.board
      case GamePhase.Battle       => PlayerBoard()
      case GamePhase.GameOver     => state.enemyBoard

    Swing.onEDT(view.update(displayBoard, if state.gamePhase == GamePhase.Positioning then state.selectedShip else None))

  def onCellClick(pos: Position): Unit = {
    state.gamePhase match
      case GamePhase.Positioning  => handleCellAction(pos)(positioning.moveShip)
      case GamePhase.Battle       => handleBattleClick(pos)
      case GamePhase.GameOver     => println("Game is over, no actions allowed")
  }

  private def handleBattleClick(pos: Position): Unit =
    val (newShipAttack, attackResult) = state.shipAttack.attack(pos)

    attackResult match
      case Right(result) =>
        state = state.copy(shipAttack = newShipAttack)

        result match
          case AttackResult.Miss =>
            println(s"Miss at $pos!")
          case AttackResult.Hit(ship) =>
            println(s"Hit ${ship} at $pos!")
          case AttackResult.Sunk(ship) =>
            println(s"Sunk ${ship}!")
          case AttackResult.AlreadyAttacked =>
            println(s"Already attacked position $pos")

        updateView()

      case Left(error) =>
        println(s"Attack error: $error")

  def onCellDoubleClick(pos: Position): Unit =
    if state.gamePhase == GamePhase.Positioning then
      handleCellAction(pos) { (board, ship, _) => positioning.rotateShip(board, ship) }

  def onKeyBoardClick(ships: List[Ship]): Unit =
    if state.gamePhase == GamePhase.Positioning then
      handleKeyboardClick(ships, state.board) match
        case Right(newState) =>
          state = newState
          updateView()
        case Left(_) =>
          println("Error randomizing ships")

  def onStartGame(): Unit =
    state.gamePhase match
      case GamePhase.Positioning =>
        state = state.copy(gamePhase = Battle, enemyBoard = positioning.randomPositioning(PlayerBoard(), state.board.getShips.toList).getOrElse(PlayerBoard()))
        println("Battle started! Find and sink all enemy ships!")
        updateView()
      case GamePhase.Battle =>
        println("Game already started, cannot start again")
      case GamePhase.GameOver =>
        println("Game over")
