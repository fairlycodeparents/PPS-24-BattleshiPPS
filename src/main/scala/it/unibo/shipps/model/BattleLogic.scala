package it.unibo.shipps.model

import it.unibo.shipps.controller.*
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.player.{BotPlayer, HumanPlayer, Player}
import it.unibo.shipps.model.ship.Ship
import it.unibo.shipps.model.{AttackResult, HumanAttackStrategy, RandomBotAttackStrategy, ShipAttack, Turn}
import it.unibo.shipps.view.renderer.ColorScheme

import scala.annotation.tailrec

/** BattleLogic handles the logic for processing battle actions during the game.
  * It updates the game state and manages attack results.
  */
object BattleLogic:

  case class BattleClickResult(
      newState: GameState,
      messages: List[String],
      shouldChangeTurn: Boolean
  )

  /** Performs a human attack, handling AlreadyAttacked cases by ignoring the attack.
    * @param player   the human player
    * @param board    the board to attack
    * @param position the position to attack
    * @return original board (unchanged) and attack result for AlreadyAttacked, or updated board and valid result
    */
  private def performHumanAttack(
      player: Player,
      board: PlayerBoard,
      position: Position
  ): (PlayerBoard, Either[String, AttackResult]) = {
    val (updatedBoard, result) = player.makeAttack(board, Some(position))

    result match {
      case Right(AttackResult.AlreadyAttacked) =>
        (board, Left(s"Position $position already attacked. Please choose another position."))
      case _ =>
        (updatedBoard, result)
    }
  }

  private def findAttackedPosition(originalBoard: PlayerBoard, updatedBoard: PlayerBoard): Option[Position] =
    val newHits = updatedBoard.hits -- originalBoard.hits
    newHits.headOption

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
      case AttackResult.Miss            => s"Miss at (${pos.col},${pos.row})!"
      case AttackResult.Hit(ship)       => s"Hit ${ship.shipType} at (${pos.col},${pos.row})!"
      case AttackResult.Sunk(ship)      => s"Sunk ${ship.shipType} anchored in (${ship.anchor.col},${ship.anchor.row})!"
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

  /** Processes a click during the battle phase.
    *
    * @param state  the current game state
    * @param player the player who attack
    * @param turn   the turn of the player
    * @param pos    the position of the click
    * @return BattleClickResult containing the updated state, messages, and whether the turn should change
    */
  def processBattleClick(
      state: GameState,
      player: Player,
      turn: Turn,
      pos: Option[Position]
  ): BattleClickResult =
    val (targetBoard, isAttackingEnemyBoard) = turn match
      case Turn.FirstPlayer  => (state.enemyBoard, true)
      case Turn.SecondPlayer => (state.board, false)

    val (updatedBoard, attackResult) = if player.isABot then
      performBotAttack(player, targetBoard)
    else
      pos match
        case Some(position) => performHumanAttack(player, targetBoard, position)
        case None           => (targetBoard, Left("Position required for human player"))

    attackResult match
      case Right(result) =>
        val newState = if isAttackingEnemyBoard then
          state.copy(enemyBoard = updatedBoard)
        else
          state.copy(board = updatedBoard)
        val attackPosition = pos.getOrElse {
          findAttackedPosition(targetBoard, updatedBoard) match {
            case Some(position) => position
            case _ =>
              updatedBoard.hits.headOption.getOrElse {
                throw new IllegalStateException("No attacked position found and no fallback available")
              }
          }
        }
        val (finalState, message) = processAttackResult(turn, newState, attackPosition, result)
        BattleClickResult(finalState, List(message), shouldChangeTurn = true)
      case Left(errorMessage) =>
        BattleClickResult(state, List(errorMessage), shouldChangeTurn = false)
