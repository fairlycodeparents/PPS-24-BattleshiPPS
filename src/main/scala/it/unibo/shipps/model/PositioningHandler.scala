package it.unibo.shipps.model

import it.unibo.shipps.controller.GameState
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.*

/** Handles ship positioning logic and user interactions during positioning phase */
object PositioningHandler:

  /** Selects a ship at the given position
    * @param gameState current game state
    * @param position position to check for ship
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state with selected ship
    */
  private def selectShipAt(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    val currentBoard = getBoardForTurn(gameState, turn)
    positioning.getShipAt(currentBoard, position) match
      case Right(ship) => gameState.selectShip(ship)
      case Left(_)     => gameState

  /** Moves the selected ship to a new position
    * @param gameState current game state
    * @param ship ship to move
    * @param position target position
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state
    */
  private def moveSelectedShip(
      gameState: GameState,
      ship: Ship,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    val currentBoard = getBoardForTurn(gameState, turn)
    positioning.moveShip(currentBoard, ship, position) match
      case Right(updatedBoard) =>
        updateBoardForTurn(gameState, updatedBoard, turn).copy(selectedShip = None)
      case Left(_) =>
        gameState

  /** Rotates the selected ship
    * @param gameState current game state
    * @param ship ship to rotate
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state with rotated ship
    */
  private def rotateSelectedShip(
      gameState: GameState,
      ship: Ship,
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    val currentBoard = getBoardForTurn(gameState, turn)
    positioning.rotateShip(currentBoard, ship) match
      case Right(updatedBoard) =>
        updateBoardForTurn(gameState, updatedBoard, turn).copy(selectedShip = None)
      case Left(_) =>
        gameState

  /** Gets the board for the current turn
    * @param gameState current game state
    * @param turn current turn
    * @return the board for the current player
    */
  private def getBoardForTurn(gameState: GameState, turn: Turn): PlayerBoard =
    turn match
      case Turn.FirstPlayer  => gameState.board
      case Turn.SecondPlayer => gameState.enemyBoard

  /** Updates the board for the current turn
    * @param gameState current game state
    * @param newBoard new board state
    * @param turn current turn
    * @return updated game state
    */
  private def updateBoardForTurn(gameState: GameState, newBoard: PlayerBoard, turn: Turn): GameState =
    turn match
      case Turn.FirstPlayer  => gameState.copy(board = newBoard)
      case Turn.SecondPlayer => gameState.copy(enemyBoard = newBoard)

  /** Handles cell click during positioning phase
    * @param gameState current game state
    * @param position clicked position
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state
    */
  def handlePositioningClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    gameState.selectedShip match
      case None =>
        selectShipAt(gameState, position, turn, positioning)
      case Some(ship) =>
        moveSelectedShip(gameState, ship, position, turn, positioning)

  /** Handles cell double click to rotate ship during positioning phase
    * @param gameState current game state
    * @param position clicked position
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state
    */
  def handlePositioningDoubleClick(
      gameState: GameState,
      position: Position,
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    gameState.selectedShip match
      case Some(ship) =>
        rotateSelectedShip(gameState, ship, turn, positioning)
      case None =>
        val stateWithSelection = selectShipAt(gameState, position, turn, positioning)
        stateWithSelection.selectedShip match
          case Some(selectedShip) =>
            rotateSelectedShip(stateWithSelection, selectedShip, turn, positioning)
          case None =>
            gameState

  /** Handles keyboard input for randomizing ship positions
    * @param gameState current game state
    * @param ships list of ships to randomize
    * @param turn current turn
    * @param positioning ship positioning logic
    * @return updated game state
    */
  def handleRandomizePositions(
      gameState: GameState,
      ships: List[Ship],
      turn: Turn,
      positioning: ShipPositioning
  ): GameState =
    positioning.randomPositioning(PlayerBoard(), ships) match
      case Right(newBoard) =>
        updateBoardForTurn(gameState, newBoard, turn).copy(selectedShip = None)
      case Left(_) =>
        gameState
