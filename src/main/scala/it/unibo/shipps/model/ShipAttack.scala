package it.unibo.shipps.model

/** Represents the result of an attack on a position. */
enum AttackResult:
  case Miss
  case Hit(ship: Ship)
  case Sunk(ship: Ship)
  case AlreadyAttacked

object ShipAttack:
  /** Performs an attack on the given [[PlayerBoard]] at the given [[Position]]. */
  def attack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    validateAttack(board, position)
      .map(_ => processValidAttack(board, position))
      .getOrElse(handleInvalidAttack(board, position))

  private def validateAttack(board: PlayerBoard, position: Position): Option[Unit] =
    Option.when(!board.hitPositons.contains(position) && isValidPosition(position))(())

  private def handleInvalidAttack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    if board.hitPositons.contains(position) then
      (board, Right(AttackResult.AlreadyAttacked))
    else
      (board, Left("Invalid attack position"))

  private def processValidAttack(board: PlayerBoard, position: Position): (PlayerBoard, Either[String, AttackResult]) =
    val newBoard = board.hit(position)
    val result = board.shipAtPosition(position)
      .map(attackShip(board, position))
      .getOrElse(Right(AttackResult.Miss))
    (newBoard, result)

  private def attackShip(board: PlayerBoard, position: Position)(ship: Ship): Either[String, AttackResult] =
    for
      damagedShip <- Right(findDamagedShip(board, ship))
      updatedShip <- damagedShip.hit(position).toRight("Invalid attack")
    yield determineAttackResult(updatedShip)

  private def findDamagedShip(board: PlayerBoard, ship: Ship): DamagedShip =
    damagedShips(board)
      .find(_.ship == ship)
      .getOrElse(DamagedShip(ship, Set.empty))

  private def determineAttackResult(damagedShip: DamagedShip): AttackResult =
    if damagedShip.isSunk then AttackResult.Sunk(damagedShip.ship)
    else AttackResult.Hit(damagedShip.ship)

  /** Returns all damaged ships on the board. */
  def damagedShips(board: PlayerBoard): Set[DamagedShip] =
    board.getShips.view
      .map(ship => ship -> board.hitPositons.intersect(ship.positions))
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
  /** @return true if the [[Ship]] is completely sunk. */
  def isSunk: Boolean = hitPositions == ship.positions

  /** Adds a hit to the ship if the position belongs to it. */
  def hit(position: Position): Option[DamagedShip] =
    Option.when(ship.positions.contains(position))(
      copy(hitPositions = hitPositions + position)
    )