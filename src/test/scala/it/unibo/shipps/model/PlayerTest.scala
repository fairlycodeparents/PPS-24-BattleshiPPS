package it.unibo.shipps.model

import it.unibo.shipps.model.board.PlayerBoardBuilder.*
import it.unibo.shipps.model.PlayerFactory.*
import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.model.board.{PlayerBoard, PlayerBoardBuilder}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class PlayerTest extends AnyFlatSpec with should.Matchers:
  val humanPlayer: Player     = createHumanPlayer("player1")
  val randomBotPlayer: Player = createBotPlayer(RandomBotAttackStrategy())
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
    randomBotPlayer.isABot shouldBe true
    randomBotPlayer shouldBe BotPlayer(RandomBotAttackStrategy())
    val averageBotPlayer = createBotPlayer(AverageBotAttackStrategy())
    averageBotPlayer.isABot shouldBe true
    averageBotPlayer shouldBe BotPlayer(AverageBotAttackStrategy())

  it should "be able to attack randomly without providing a position" in:
    val (_, result) = randomBotPlayer.makeAttack(enemyBoard)
    result.isRight shouldBe true

  it should "not be able to attack a given position" in:
    val (_, result) = randomBotPlayer.makeAttack(enemyBoard, Option(A(1)))
    result.isRight shouldBe false

  "There" should "be possible to calculate adjacent positions" in:
    object TestCalculator extends AdjacentPositionsCalculator
    val expected = List(
      B(1),
      A(2)
    )
    TestCalculator.getAdjacentPositions(A(1)) shouldBe expected
