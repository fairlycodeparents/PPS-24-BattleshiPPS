package it.unibo.shipps.controller

import it.unibo.shipps.controller.GamePhase.{Battle, Positioning}
import it.unibo.shipps.controller.Turn.FirstPlayer
import it.unibo.shipps.model.*
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.player.Player
import it.unibo.shipps.model.ship.Ship
import it.unibo.shipps.view.SimpleGui
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

  /** Randomizes the player's board based on the turn.
    * @param newBoard the new board to set
    * @param turn the current turn of the game
    * @return updated GameState with the new board and no selected ship
    */
  def randomizeBoard(newBoard: PlayerBoard, turn: Turn): GameState =
    if turn == Turn.FirstPlayer then
      copy(board = newBoard, selectedShip = None)
    else
      copy(enemyBoard = newBoard, selectedShip = None)

  /** Moves the selected ship to a new position on the board.
    * @param newBoard the new board with the ship moved
    * @param turn the current turn of the game
    * @return updated GameState with the ship moved and no selected ship
    */
  def moveShipTo(newBoard: PlayerBoard, turn: Turn): GameState = {
    if turn == Turn.FirstPlayer then
      copy(board = newBoard, selectedShip = None)
    else
      copy(enemyBoard = newBoard, selectedShip = None)
  }

  /** Rotates the selected ship on the board.
    * @param newBoard the new board with the ship rotated
    * @param turn the current turn of the game
    * @return updated GameState with the ship rotated and no selected ship
    */
  def rotateShipTo(newBoard: PlayerBoard, turn: Turn): GameState = {
    if turn == Turn.FirstPlayer then
      copy(board = newBoard, selectedShip = None)
    else
      copy(enemyBoard = newBoard, selectedShip = None)
  }

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
  * @param positioning the ship positioning logic
  * @param view the GUI view for displaying the game state
  */
class GameController(
    initialBoard: PlayerBoard,
    enemyBoard: PlayerBoard,
    firstPlayer: Player,
    secondPlayer: Player,
    val positioning: ShipPositioning,
    var view: SimpleGui
):

  /** The initial game state, containing the boards and the current phase of the game. */
  var state: GameState = GameState(initialBoard, enemyBoard, None, Positioning)

  private var turn: Turn                          = Turn.FirstPlayer
  private var waitingDialog: Option[JDialog]      = None
  private var isPositioningPhaseComplete: Boolean = false

  private def handleCellAction(currentState: GameState, pos: Position)(
      shipAction: (PlayerBoard, Ship, Position) => Either[String, PlayerBoard]
  ): GameState =
    if state.gamePhase == GamePhase.Positioning then {
      currentState.selectedShip match
        case None =>
          val boardToCheck = getCurrentPlayerBoard(currentState)
          positioning.getShipAt(boardToCheck, pos) match
            case Right(ship) => currentState.selectShip(ship)
            case Left(_)     => currentState
        case Some(ship) =>
          val boardToUpdate = getCurrentPlayerBoard(currentState)
          shipAction(boardToUpdate, ship, pos) match
            case Right(updatedBoard) =>
              val newState = updateCurrentPlayerBoard(currentState, updatedBoard)
              if shipAction == positioning.moveShip then
                newState.moveShipTo(updatedBoard, turn)
              else
                newState.rotateShipTo(updatedBoard, turn)
            case Left(_) => currentState
    } else currentState

  private def getCurrentPlayerBoard(state: GameState): PlayerBoard = {
    turn match {
      case Turn.FirstPlayer  => state.board
      case Turn.SecondPlayer => state.enemyBoard
    }
  }

  private def updateCurrentPlayerBoard(state: GameState, newBoard: PlayerBoard): GameState = {
    turn match {
      case Turn.FirstPlayer  => state.copy(board = newBoard)
      case Turn.SecondPlayer => state.copy(enemyBoard = newBoard)
    }
  }

  private def handleBattleClick(currentState: GameState, pos: Option[Position]): (GameState, List[String]) = {
    if turn == Turn.FirstPlayer then
      BattleLogic.processBattleClick(currentState, firstPlayer, Turn.FirstPlayer, pos)
    else
      BattleLogic.processBattleClick(currentState, secondPlayer, Turn.SecondPlayer, pos)
  }

  def showTurnDialog(playerName: String): Unit = {
    Swing.onEDT {
      val dialog = new JDialog(view.peer, s"Player Turn", true)
      dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
      dialog.setSize(350, 200)
      dialog.setLocationRelativeTo(view.peer)
      dialog.setResizable(false)

      val label = new JLabel(s"<html><center>It's $playerName's turn<br><br>Click OK when ready</center></html>")
      label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

      val okButton = new javax.swing.JButton("OK")
      okButton.addActionListener(_ => {
        dialog.setVisible(false)
        dialog.dispose()
      })

      val panel = new javax.swing.JPanel(new BorderLayout())
      panel.add(label, BorderLayout.CENTER)
      panel.add(okButton, BorderLayout.SOUTH)

      dialog.add(panel)
      waitingDialog = Some(dialog)
      dialog.setVisible(true)
    }
  }

  private def showWaitingDialog(): Unit = {
    Swing.onEDT {
      val dialog = new JDialog(view.peer, "Bot Turn", true)
      dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
      dialog.setSize(300, 150)
      dialog.setLocationRelativeTo(view.peer)
      dialog.setResizable(false)

      val label = new JLabel("<html><center>Bot is thinking...<br>Please wait</center></html>")
      label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

      dialog.add(label, BorderLayout.CENTER)
      waitingDialog = Some(dialog)
      dialog.setVisible(true)
    }
  }

  private def hideWaitingDialog(): Unit = {
    Swing.onEDT {
      waitingDialog.foreach { dialog =>
        dialog.setVisible(false)
        dialog.dispose()
      }
      waitingDialog = None
    }
  }

  private def showBotResultDialog(result: String): Unit = {
    Swing.onEDT {
      val dialog = new JDialog(view.peer, "Bot Turn", true)
      dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE)
      dialog.setSize(300, 150)
      dialog.setLocationRelativeTo(view.peer)
      dialog.setResizable(false)

      val label = new JLabel(s"<html><center>Bot attack done!<br>${result}</center></html>")
      label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

      dialog.addWindowListener(new java.awt.event.WindowAdapter() {
        override def windowClosing(e: java.awt.event.WindowEvent): Unit = {
          endBotTurn()
        }
      })

      dialog.add(label, BorderLayout.CENTER)
      waitingDialog = Some(dialog)
      dialog.setVisible(true)
    }
  }

  private def endBotTurn(): Unit = {
    if state.gamePhase != GamePhase.GameOver then
      turn = Turn.FirstPlayer
      updateView(turn)
      if !firstPlayer.isABot then
        showTurnDialog("Player 1")
  }

  private def executeBotTurn(): String = {
    if turn == Turn.SecondPlayer && state.gamePhase == GamePhase.Battle then
      val (newState, messages) = handleBattleClick(state, None)
      messages.foreach(println)

      state = newState
      updateView(Turn.SecondPlayer)
      messages.last
    else "error! not bot turn"
  }

  private def executeWithDelay(action: () => Unit, delayMs: Int = 2000): Unit = {
    val timer = new Timer(
      delayMs,
      _ => {
        action()
      }
    )
    timer.setRepeats(false)
    timer.start()
  }

  private def updateView(turn: Turn): Unit =
    val (displayBoard, displayEnemyBoard) = state.gamePhase match {
      case GamePhase.Positioning =>
        turn match {
          case Turn.FirstPlayer  => (state.board, state.enemyBoard)
          case Turn.SecondPlayer => (state.board, state.enemyBoard)
        }
      case GamePhase.Battle | GamePhase.GameOver =>
        turn match {
          case Turn.FirstPlayer =>
            (state.board, state.enemyBoard)
          case Turn.SecondPlayer =>
            (state.board, state.enemyBoard)
        }
    }

    state = state.copy(board = displayBoard, enemyBoard = displayEnemyBoard)

    Swing.onEDT(view.update(turn))

  /** Handles the click on a cell based on the current game phase.
    * @param pos the position of the cell clicked
    */
  def onCellClick(pos: Position): Unit = {
    val newState = state.gamePhase match
      case GamePhase.Positioning =>
        val result = handleCellAction(state, pos)(positioning.moveShip)
        result
      case GamePhase.Battle =>
        if turn == Turn.FirstPlayer then
          val (updatedState, messages) = handleBattleClick(state, Some(pos))
          messages.foreach(println)
          if updatedState.gamePhase == GamePhase.GameOver then
            updatedState
          else
            turn = Turn.SecondPlayer
            if secondPlayer.isABot then
              showWaitingDialog()
              executeWithDelay(() => {
                if secondPlayer.isABot && state.gamePhase == GamePhase.Battle then
                  val result = executeBotTurn()
                  hideWaitingDialog()
                  showBotResultDialog(result)
              })
            else
              showTurnDialog("Player 2")
            updatedState
        else if !secondPlayer.isABot then
          val (updatedState, messages) = handleBattleClick(state, Some(pos))
          messages.foreach(println)
          if updatedState.gamePhase == GamePhase.GameOver then
            updatedState
          else
            turn = Turn.FirstPlayer
            showTurnDialog("Player 1")
            updatedState
        else
          println("It's not your turn, wait for the bot to play")
          state
      case GamePhase.GameOver =>
        println("Game is over, no actions allowed")
        state

    state = newState
    updateView(turn)
  }

  /** Handles the double click on a cell to rotate the ship during the positioning phase.
    * @param pos the position of the cell double-clicked
    */
  def onCellDoubleClick(pos: Position): Unit =
    if state.gamePhase == GamePhase.Positioning then
      val newState = handleCellAction(state, pos) { (board, ship, _) => positioning.rotateShip(board, ship) }
      state = newState
      updateView(turn)

  /** Handles the keyboard click to randomize ship positioning during the positioning phase.
    * @param ships the list of ships to randomize
    */
  def onKeyBoardClick(ships: List[Ship]): Unit =
    if state.gamePhase == GamePhase.Positioning then
      positioning.randomPositioning(PlayerBoard(), ships) match
        case Right(newBoard) =>
          state = updateCurrentPlayerBoard(state, newBoard).randomizeBoard(newBoard, turn)
          updateView(turn)
        case Left(error) =>
          println("Error randomizing ships")

  /** Handles the action when the game starts. */
  def onStartGame(): Unit = {
    val (newState, message) = state.gamePhase match
      case GamePhase.Positioning =>
        if secondPlayer.isABot then
          val enemyBoard = positioning.randomPositioning(PlayerBoard(), state.board.ships.toList)
            .getOrElse(PlayerBoard())
          val updatedState = state.copy(enemyBoard = enemyBoard)
          (updatedState.startBattle(enemyBoard), "Battle started! Find and sink all enemy ships!")
        else if !isPositioningPhaseComplete then
          turn = Turn.SecondPlayer
          isPositioningPhaseComplete = true
          showTurnDialog("Player 2 - position your ships")
          (state, "Player 2: Position your ships and press Start Game again")
        else
          turn = Turn.FirstPlayer
          showTurnDialog("Player 1")
          (state.startBattle(state.enemyBoard), "Battle started! Player 1 attacks first!")
      case GamePhase.Battle =>
        (state, println("Game already started, cannot start again"))
      case GamePhase.GameOver =>
        (state, println("Game over"))

    state = newState
    updateView(turn)
  }
