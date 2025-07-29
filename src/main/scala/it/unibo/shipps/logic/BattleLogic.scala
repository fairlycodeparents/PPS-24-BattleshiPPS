package it.unibo.shipps.logic

import it.unibo.shipps.controller.*
import it.unibo.shipps.model.{AttackResult, Position, Ship, ShipAttack}

object BattleLogic {
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
    state.copy(attackResult = updatedAttackResults)
}
