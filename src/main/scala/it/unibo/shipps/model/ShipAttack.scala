package it.unibo.shipps.model

import it.unibo.shipps.model.board.{PlayerBoard, Position}

/** Represents the result of an attack on a position. */
enum AttackResult:
  case Miss
  case Hit(ship: Ship)
  case Sunk(ship: Ship)
  case EndOfGame(ship: Ship)
  case AlreadyAttacked

object ShipAttack:
  /** Performs an attack on the given [[PlayerBoard]] at the given [[Position]]
    * @param board the enemy board to attack
    * @param position the [[Position]] to attack
    * @return updated [[PlayerBoard]] and either error message or [[AttackResult]]
    */
  def attack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    validateAttack(board, position)
      .map(_ => processValidAttack(board, position))
      .getOrElse(handleInvalidAttack(board, position))

  private def validateAttack(board: PlayerBoard, position: Position): Option[Unit] =
    Option.when(!board.hits.contains(position) && isValidPosition(position))(())

  private def handleInvalidAttack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    if board.hits.contains(position) then
      (board, Right(AttackResult.AlreadyAttacked))
    else
      (board, Left("Invalid attack position"))

  private def processValidAttack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    val newBoard = board.hit(position)
    val result = board.shipAtPosition(position)
      .map(attackShip(newBoard, position))
      .getOrElse(Right(AttackResult.Miss))
    (newBoard, result)

  private def attackShip(board: PlayerBoard, position: Position)(ship: Ship): Either[String, AttackResult] =
    for
      damagedShip <- Right(findDamagedShip(board, ship))
      updatedShip <- damagedShip.hit(position).toRight("Invalid attack")
    yield determineAttackResult(updatedShip, board)

  private def findDamagedShip(board: PlayerBoard, ship: Ship): DamagedShip =
    damagedShips(board)
      .find(_.ship == ship)
      .getOrElse(DamagedShip(ship, Set.empty))

  private def determineAttackResult(damagedShip: DamagedShip, board: PlayerBoard): AttackResult =
    if damagedShip.isSunk then determineEndOfGame(damagedShip, board)
    else AttackResult.Hit(damagedShip.ship)

  private def determineEndOfGame(damagedShip: DamagedShip, board: PlayerBoard): AttackResult =
    if areAllShipsSunk(board) then AttackResult.EndOfGame(damagedShip.ship)
    else AttackResult.Sunk(damagedShip.ship)

  private def areAllShipsSunk(board: PlayerBoard): Boolean =
    damagedShips(board).count(damagedShip => damagedShip.isSunk) == board.ships.size

  /** Returns all the ships that have been hit at least once
    * @param board the [[PlayerBoard]] containing ships
    * @return a set of [[DamagedShip]], each representing a ship and its hit positions
    */
  def damagedShips(board: PlayerBoard): Set[DamagedShip] =
    board.ships.view
      .map(ship => ship -> board.hits.intersect(ship.positions))
      .collect { case (ship, hitPositions) if hitPositions.nonEmpty => DamagedShip(ship, hitPositions) }
      .toSet

  private def isValidPosition(position: Position): Boolean =
    position.x >= 0 && position.x < PlayerBoard.size &&
      position.y >= 0 && position.y < PlayerBoard.size

/** Represents a ship with its damage state. */
case class DamagedShip(
    ship: Ship,
    hitPositions: Set[Position]
):
  /** @return true if the [[Ship]] is completely sunk */
  def isSunk: Boolean = hitPositions == ship.positions

  /** Adds a hit to the [[Ship]] if the position belongs to it
    * @param position the [[Position]] to hit
    * @return updated [[DamagedShip]] if the position is part of the ship; None otherwise
    */
  def hit(position: Position): Option[DamagedShip] =
    Option.when(ship.positions.contains(position))(
      copy(hitPositions = hitPositions + position)
    )
