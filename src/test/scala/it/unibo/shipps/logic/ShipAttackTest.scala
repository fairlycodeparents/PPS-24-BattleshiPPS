package it.unibo.shipps.logic

import org.scalatest.*
import flatspec.*
import it.unibo.shipps.model.*
import it.unibo.shipps.model.AttackResult.*
import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.model.PlayerBoardBuilder.*
import matchers.*

class ShipAttackTest extends AnyFlatSpec with should.Matchers:
  val frigate: Ship         = Frigate.horizontalAt(A(5))
  val submarine: Ship       = Submarine.verticalAt(B(8))
  val carrier: Ship         = Carrier.verticalAt(G(1))
  val destroyer: Ship       = Destroyer.horizontalAt(H(9))
  val ships: Set[Ship]      = Set(frigate, submarine, carrier, destroyer)
  val board: PlayerBoard    = PlayerBoard(ships)

  def attack(board: PlayerBoard, pos: Position): (PlayerBoard, AttackResult) =
    val (newBoard, result) = ShipAttack.attack(board, pos)
    result should matchPattern { case Right(_) => }
    (newBoard, result.toOption.get)

  "A ship" should "be hit" in:
    val (_, result) = attack(board, A(5))
    result should matchPattern { case Hit(`frigate`) => }

  it should "be sunk after all its positions are hit" in:
    val (newBoard, _) = attack(board, A(5))
    val (_, result) = attack(newBoard, B(5))
    result shouldBe Sunk(frigate)

  it should "not be attacked many times" in:
    val (newBoard, _) = attack(board,A(5))
    val (_, result) = attack(newBoard,A(5))
    result shouldBe AlreadyAttacked

  "An attack on the open sea" should "be a Miss" in :
    val (_, result) = attack(board, A(1))
    result shouldBe Miss

  it should "not be repeated on the same position" in :
    val (newBoard, _) = attack(board, A(1))
    val (_, result) = attack(newBoard, A(1))
    result shouldBe AlreadyAttacked

  "An invalid position" should "not be chosen" in :
    val (newBoard, result) = ShipAttack.attack(board, Position(11, 11))
    result shouldBe Left("Invalid attack position")
