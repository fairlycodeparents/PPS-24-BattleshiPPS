package it.unibo.shipps.model

import it.unibo.shipps.model.AttackResult.AlreadyAttacked

/** Represents the result of an attack on a position. */
enum AttackResult:
  case Miss
  case Hit(ship: Ship)
  case Sunk(ship: Ship)
  case AlreadyAttacked

case class ShipAttack(board: PlayerBoard, damagedShips: Set[DamagedShip], attackedPositions: Set[Position] = Set.empty):

  val shipPositioning: ShipPositioning = ShipPositioningImpl

  /** Performs an attack on the given [[Position]]. */
  def attack(position: Position): (ShipAttack, Either[String, AttackResult]) =
    if attackedPositions.contains(position) then
      return (this, Right(AlreadyAttacked))
    if invalidPos(position) then
      return (this, Left("Invalid attack position"))
    shipPositioning.getShipAt(board, position) match
      case Left(_) =>
        val newGrid = copy(attackedPositions = attackedPositions + position)
        (newGrid, Right(AttackResult.Miss))
      case Right(ship) =>
        val damagedShip = findDamagedShip(ship).getOrElse(DamagedShip(ship, Set.empty))
        damagedShip.hit(position) match
          case None => (this, Left("Invalid attack"))
          case Some(updatedShip) =>
            val updatedShips = damagedShips.filterNot(_.ship == ship) + updatedShip
            val newGrid = copy(
              damagedShips = updatedShips,
              attackedPositions = attackedPositions + position
            )
            val result = if updatedShip.isSunk then
              AttackResult.Sunk(updatedShip.ship)
            else
              AttackResult.Hit(updatedShip.ship)
            (newGrid, Right(result))

  private def findDamagedShip(ship: Ship): Option[DamagedShip] =
    damagedShips.find(_.ship == ship)

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
