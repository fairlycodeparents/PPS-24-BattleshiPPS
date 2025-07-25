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
  val baseState: ShipAttack = ShipAttack(board, Set.empty, Set.empty)

  def attack(state: ShipAttack, pos: Position): AttackResult =
    val (_, result) = state.attack(pos)
    result should matchPattern { case Right(_) => }
    result.toOption.get

  "A ship" should "be hit" in:
    val result = attack(baseState, A(5))
    result should matchPattern { case Hit(`frigate`) => }

  "Attack" should "be missed on empty sea" in:
    val result = attack(baseState, A(1))
    result shouldBe Miss

  "A ship" should "be sunk after all its positions are hit" in:
    val (state1, _) = baseState.attack(A(5))
    val (_, result) = state1.attack(B(5))
    result shouldBe Right(Sunk(frigate))

  "A ship position" should "not be attacked many times" in:
    val (state1, _) = baseState.attack(A(5))
    val (_, result) = state1.attack(A(5))
    result shouldBe Right(AlreadyAttacked)

  "A sea position" should "not be attacked many times" in:
    val (state1, _) = baseState.attack(A(1))
    val (_, result) = state1.attack(A(1))
    result shouldBe Right(AlreadyAttacked)

  "An invalid position" should "not be chosen" in:
    val (newState, result) = baseState.attack(Position(11, 11))
    result shouldBe Left("Invalid attack position")
