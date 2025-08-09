package it.unibo.shipps.logic

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.*
import it.unibo.shipps.model.AttackResult.*
import it.unibo.shipps.model.ship.ShipType.*
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.board.BoardCoordinates.*
import it.unibo.shipps.model.ship.Ship
import matchers.*

class ShipAttackTest extends AnyFlatSpec with should.Matchers:
  val frigate: Ship      = Frigate.horizontalAt(A(5))
  val submarine: Ship    = Submarine.verticalAt(B(8))
  val destroyer: Ship    = Destroyer.horizontalAt(G(9))
  val carrier: Ship      = Carrier.verticalAt(G(1))
  val ships: Set[Ship]   = Set(frigate, submarine, carrier, destroyer)
  val board: PlayerBoard = PlayerBoard(ships)

  def attack(board: PlayerBoard, pos: Position): (PlayerBoard, AttackResult) =
    val (newBoard, result) = ShipAttack.attack(board, pos)
    result should matchPattern { case Right(_) => }
    (newBoard, result.toOption.get)

  "A ship" should "be hit" in:
    val (_, result) = attack(board, A(5))
    result should matchPattern { case Hit(`frigate`) => }

  it should "be sunk after all its positions are hit" in:
    val (_, result) = frigateSunk(board)
    result shouldBe Sunk(frigate)

  it should "not be attacked many times" in:
    val (newBoard, _) = attack(board, A(5))
    val (_, result)   = attack(newBoard, A(5))
    result shouldBe AlreadyAttacked

  "An attack on the open sea" should "be a Miss" in:
    val (_, result) = attack(board, A(1))
    result shouldBe Miss

  it should "not be repeated on the same position" in:
    val (newBoard, _) = attack(board, A(1))
    val (_, result)   = attack(newBoard, A(1))
    result shouldBe AlreadyAttacked

  "An invalid position" should "not be chosen" in:
    val (newBoard, result) = ShipAttack.attack(board, Position(11, 11))
    result shouldBe Left("Invalid attack position")

  "The game" should "end if all the ships are sunk" in:
    val (board1, result1) = frigateSunk(board)
    result1 shouldBe Sunk(frigate)
    val (board2, result2) = submarineSunk(board1)
    result2 shouldBe Sunk(submarine)
    val (board3, result3) = carrierSunk(board2)
    result3 shouldBe Sunk(carrier)
    val (board4, result4) = destroyerSunk(board3)
    result4 shouldBe EndOfGame(destroyer)

  def frigateSunk(initialBoard: PlayerBoard): (PlayerBoard, AttackResult) =
    val (board1, _)      = attack(initialBoard, A(5))
    val (board2, result) = attack(board1, B(5))
    (board2, result)

  def submarineSunk(initialBoard: PlayerBoard): (PlayerBoard, AttackResult) =
    val (board1, _)      = attack(initialBoard, B(8))
    val (board2, _)      = attack(board1, B(9))
    val (board3, result) = attack(board2, B(10))
    (board3, result)

  def carrierSunk(initialBoard: PlayerBoard): (PlayerBoard, AttackResult) =
    val (board1, _)      = attack(initialBoard, G(1))
    val (board2, _)      = attack(board1, G(2))
    val (board3, _)      = attack(board2, G(3))
    val (board4, _)      = attack(board3, G(4))
    val (board5, result) = attack(board4, G(5))
    (board5, result)

  def destroyerSunk(initialBoard: PlayerBoard): (PlayerBoard, AttackResult) =
    val (board1, _)      = attack(initialBoard, G(9))
    val (board2, _)      = attack(board1, H(9))
    val (board3, _)      = attack(board2, I(9))
    val (board4, result) = attack(board3, J(9))
    (board4, result)
