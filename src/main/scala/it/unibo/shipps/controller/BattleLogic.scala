package it.unibo.shipps.controller

import it.unibo.shipps.controller.*
import it.unibo.shipps.model.{AttackResult, Position, Ship, ShipAttack}
import it.unibo.shipps.view.renderer.ColorScheme

/** BattleLogic handles the logic for processing clicks during the battle phase of the game. */
object BattleLogic {

  /** Processes a click during the battle phase.
    * @param state the current game state
    * @param pos the position of the click
    * @return updated game state and a list of messages indicating the result of the click
    */
  def processBattleClick(
      state: GameState,
      pos: Position
  ): (GameState, List[String]) = {
    val (newEnemyBoard, attackResult) = ShipAttack.attack(state.enemyBoard, pos)
    attackResult match
      case Right(result) =>
        val newState              = state.copy(enemyBoard = newEnemyBoard)
        val (finalState, message) = processAttackResult(newState, pos, result)
        (finalState, List(message))
      case Left(errorMessage) =>
        (state, List(errorMessage))
  }

  private def processAttackResult(
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
        updateSunkShipResult(state, ship, result)
      case AttackResult.EndOfGame(ship) =>
        updateSunkShipResult(state, ship, result)
          .copy(gamePhase = GamePhase.GameOver)
      case _ =>
        state.addAttackResult(pos, result)

    (updatedState, message)
  }

  private def updateSunkShipResult(state: GameState, ship: Ship, sunkResult: AttackResult): GameState =
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
}
