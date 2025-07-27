package it.unibo.shipps.model

import it.unibo.shipps.model.AttackResult.AlreadyAttacked

/** Represents the result of an attack on a position. */
enum AttackResult:
  case Miss
  case Hit(ship: Ship)
  case Sunk(ship: Ship)
  case AlreadyAttacked

object ShipAttack:
  /** Performs an attack on the given [[PlayerBoard]] at the given [[Position]]. */
  def attack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    if board.hitPositons.contains(position) then
      return (board, Right(AlreadyAttacked))
    if invalidPos(position) then
      return (board, Left("Invalid attack position"))
    board.shipAtPosition(position) match
      case None =>
        val newBoard = board.hit(position)
        (newBoard, Right(AttackResult.Miss))
      case Some(ship) =>
        val damagedShip = findDamagedShip(board, ship).getOrElse(DamagedShip(ship, Set.empty))
        damagedShip.hit(position) match
          case None => (board, Left("Invalid attack"))
          case Some(updatedShip) =>
            val updatedShips = damagedShips(board).filterNot(_.ship == ship) + updatedShip
            val newBoard = board.hit(position)
            val result = if updatedShip.isSunk then
              AttackResult.Sunk(updatedShip.ship)
            else
              AttackResult.Hit(updatedShip.ship)
            (newBoard, Right(result))

  def damagedShips(board: PlayerBoard): Set[DamagedShip] =
    board.getShips.flatMap { ship =>
      val hitPositionsForShip = board.hitPositons.intersect(ship.positions)
      Option.when(hitPositionsForShip.nonEmpty)(DamagedShip(ship, hitPositionsForShip))
    }

  private def findDamagedShip(board: PlayerBoard, ship: Ship): Option[DamagedShip] =
    damagedShips(board).find(_.ship == ship)

  private def invalidPos(pos: Position): Boolean =
    pos.x < 0 || pos.x >= PlayerBoard.size || pos.y < 0 || pos.y >= PlayerBoard.size

/** Represents a ship with its damage state. */
case class DamagedShip(
    ship: Ship,
    hitPositions: Set[Position]
):
  /** @return true if the [[Ship]] is completely sunk. */
  def isSunk: Boolean = hitPositions == ship.positions

  /** Adds a hit to the ship if the position belongs to it. */
  def hit(position: Position): Option[DamagedShip] =
    Option.when(ship.positions.contains(position))(
      copy(hitPositions = hitPositions + position)
    )
