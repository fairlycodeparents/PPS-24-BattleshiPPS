package it.unibo.shipps.controller

import it.unibo.shipps.controller.*
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.{
  AttackResult,
  BotPlayer,
  HumanAttackStrategy,
  HumanPlayer,
  Player,
  RandomBotAttackStrategy,
  Ship,
  ShipAttack
}
import it.unibo.shipps.view.renderer.ColorScheme

import scala.annotation.tailrec

/** BattleLogic handles the logic for processing battle actions during the game.
  * It updates the game state and manages attack results.
  */
object BattleLogic:

  /** Processes a click during the battle phase.
    * @param state the current game state
    * @param player the player who attack
    * @param turn the turn of the player
    * @param pos the position of the click
    * @return updated game state and a list of messages indicating the result of the click
    */
  def processBattleClick(
      state: GameState,
      player: Player,
      turn: Turn,
      pos: Option[Position]
  ): (GameState, List[String]) = {
    val (targetBoard, isAttackingEnemyBoard) = turn match {
      case Turn.FirstPlayer  => (state.enemyBoard, true)
      case Turn.SecondPlayer => (state.board, false)
    }

    val (updatedBoard, attackResult) = if player.isABot then
      performBotAttack(player, targetBoard)
    else
      pos match {
        case Some(position) => player.makeAttack(targetBoard, Some(position))
        case None           => (targetBoard, Left("Position required for human player"))
      }

    attackResult match
      case Right(result) =>
        val newState = if isAttackingEnemyBoard then
          state.copy(enemyBoard = updatedBoard)
        else
          state.copy(board = updatedBoard)

        val (finalState, message) = processAttackResult(turn, newState, pos.orNull, result)
        (finalState, List(message))
      case Left(errorMessage) =>
        (state, List(errorMessage))
  }

  /** Performs a bot attack with retry logic for AlreadyAttacked cases.
    * @param player     the bot player
    * @param board      the board to attack
    * @param maxRetries maximum number of retry attempts
    * @return updated board and attack result
    */
  private def performBotAttack(
      player: Player,
      board: PlayerBoard,
      maxRetries: Int = 100
  ): (PlayerBoard, Either[String, AttackResult]) = {

    @tailrec
    def attemptAttack(currentBoard: PlayerBoard, retriesLeft: Int): (PlayerBoard, Either[String, AttackResult]) = {
      if (retriesLeft <= 0) {
        return (currentBoard, Left("Bot failed to find valid attack position after maximum retries"))
      }

      val (updatedBoard, result) = player.makeAttack(currentBoard, None)

      result match {
        case Right(AttackResult.AlreadyAttacked) =>
          attemptAttack(currentBoard, retriesLeft - 1)
        case _ =>
          (updatedBoard, result)
      }
    }

    attemptAttack(board, maxRetries)
  }

  /** Processes the result of an attack and updates the game state.
    * @param turn the current turn
    * @param state the current game state
    * @param pos the position attacked
    * @param result the result of the attack
    * @return updated game state and a message indicating the result of the attack
    */
  private def processAttackResult(
      turn: Turn,
      state: GameState,
      pos: Position,
      result: AttackResult
  ): (GameState, String) = {
    val message = result match
      case AttackResult.Miss            => s"Miss at $pos!"
      case AttackResult.Hit(ship)       => s"Hit ${ship} at $pos!"
      case AttackResult.Sunk(ship)      => s"Sunk ${ship}!"
      case AttackResult.EndOfGame(_)    => "Game over! All enemy ships sunk!"
      case AttackResult.AlreadyAttacked => s"Already attacked position $pos"

    val updatedState = result match
      case AttackResult.Sunk(ship) =>
        updateSunkShipResult(turn, state, ship, result)
      case AttackResult.EndOfGame(ship) =>
        updateSunkShipResult(turn, state, ship, result)
          .copy(gamePhase = GamePhase.GameOver)
      case _ =>
        turn match {
          case Turn.FirstPlayer  => state.addAttackResult(pos, result)
          case Turn.SecondPlayer => state.addEnemyAttackResult(pos, result)
        }

    (updatedState, message)
  }

  /** Updates the game state when a ship is sunk.
    * @param turn the current turn
    * @param state the current game state
    * @param ship the sunk ship
    * @param sunkResult the result of the attack that sunk the ship
    * @return updated game state with sunk ship results
    */
  private def updateSunkShipResult(turn: Turn, state: GameState, ship: Ship, sunkResult: AttackResult): GameState =
    turn match {
      case Turn.FirstPlayer =>
        val updatedAttackResults = ship.positions.foldLeft(state.attackResult) { (results, position) =>
          results + (position -> sunkResult)
        }
        val updatedCellColors = ship.positions.foldLeft(state.cellColors) { (colors, position) =>
          colors + (position -> ColorScheme.SUNK)
        }
        state.copy(
          attackResult = updatedAttackResults,
          cellColors = updatedCellColors
        )

      case Turn.SecondPlayer =>
        val updatedAttackResults = ship.positions.foldLeft(state.enemyAttackResult) { (results, position) =>
          results + (position -> sunkResult)
        }
        val updatedCellColors = ship.positions.foldLeft(state.enemyCellColors) { (colors, position) =>
          colors + (position -> ColorScheme.SUNK)
        }
        state.copy(
          enemyAttackResult = updatedAttackResults,
          enemyCellColors = updatedCellColors
        )
    }
