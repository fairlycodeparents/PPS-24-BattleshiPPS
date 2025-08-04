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

/** BattleLogic handles the logic for processing clicks during the battle phase of the game. */
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
      player.makeAttack(targetBoard, None)
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

        // val attackPos = pos.getOrElse(findLastAttackedPosition(targetBoard, updatedBoard))
        val (finalState, message) = processAttackResult(turn, newState, pos.get, result)
        (finalState, List(message))
      case Left(errorMessage) =>
        (state, List(errorMessage))
  }

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
