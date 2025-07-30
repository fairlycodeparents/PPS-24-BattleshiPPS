package it.unibo.shipps.model

import it.unibo.shipps.model.PlayerBoardBuilder.*
import it.unibo.shipps.model.PlayerFactory.*
import it.unibo.shipps.model.ShipType.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.language.postfixOps

class PlayerTest extends AnyFlatSpec with should.Matchers:
  val humanPlayer: Player = createHumanPlayer("player1")
  val botPlayer: Player   = createBotPlayer(RandomBotAttackStrategy())
  val enemyBoard: PlayerBoard = PlayerBoardBuilder.board(
    place a Frigate at G(1) vertical,
    place a Submarine at A(5) horizontal,
    place a Destroyer at C(7) horizontal,
    place a Carrier at J(2) vertical
  )
  print(enemyBoard)

  "A human player" should "be created with its own attack strategy" in:
    humanPlayer.isABot shouldBe false
    humanPlayer shouldBe HumanPlayer("player1", HumanAttackStrategy())

  it should "be able to attack" in:
    val (_, result) = humanPlayer.makeAttack(enemyBoard, Option(G(1)))
    result.isRight shouldBe true

  it should "not be able to attack without providing a position" in:
    val (_, result) = humanPlayer.makeAttack(enemyBoard, Option.empty)
    result.isRight shouldBe false

  "A bot player" should "be created with an attack strategy" in:
    botPlayer.isABot shouldBe true
    botPlayer shouldBe BotPlayer(RandomBotAttackStrategy())
