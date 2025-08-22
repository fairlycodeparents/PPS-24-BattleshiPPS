package it.unibo.shipps.model

import it.unibo.shipps.model.player.PlayerFactory.*
import ShipType.*
import it.unibo.shipps.model.AttackResult.*
import it.unibo.shipps.model.board.ShipPlacementDSL.place
import it.unibo.shipps.model.board.BoardCoordinates.*
import it.unibo.shipps.model.board.{PlayerBoard, PlayerBoardBuilder}
import it.unibo.shipps.model.player.{BotPlayer, HumanPlayer, Player}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers.defined
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

  "A human player" should "be created with its own attack strategy" in:
    humanPlayer.isABot shouldBe false

  it should "be able to attack" in:
    val (_, result) = humanPlayer.makeAttack(enemyBoard, Option(G(1)))
    result.isRight shouldBe true

  it should "not be able to attack without providing a position" in:
    val (_, result) = humanPlayer.makeAttack(enemyBoard, Option.empty)
    result.isRight shouldBe false

  "A bot player" should "be created with an attack strategy" in:
    randomBotPlayer.isABot shouldBe true
    val averageBotPlayer = createBotPlayer(AverageBotAttackStrategy())
    averageBotPlayer.isABot shouldBe true

  it should "be able to attack randomly without providing a position" in:
    val (_, result) = randomBotPlayer.makeAttack(enemyBoard)
    result.isRight shouldBe true

  it should "not be able to attack a given position" in:
    val (_, result) = randomBotPlayer.makeAttack(enemyBoard, Option(A(1)))
    result.isRight shouldBe false

  "An average smart bot" should "be able to hit an adjacent position after a hit" in:
    val bot               = createBotPlayer(AverageBotAttackStrategy())
    val (updatedBoard, _) = ShipAttack.attack(enemyBoard, G(1))
    val expectedAdjacent  = List(F(1), H(1), G(2))
    val (finalBoard, res) = bot.makeAttack(updatedBoard)
    val attackedPosition  = finalBoard.hits.diff(updatedBoard.hits).headOption
    attackedPosition shouldBe defined
    expectedAdjacent should contain(attackedPosition.get)

  it should "be able to sink a ship after a hit" in:
    val bot               = createBotPlayer(AverageBotAttackStrategy())
    val targetShip        = Submarine.horizontalAt(A(5))
    val (updatedBoard, _) = ShipAttack.attack(enemyBoard, B(5))
    val (finalBoard, res) = executeAttacksUntilSunk(
      bot,
      updatedBoard,
      targetShip,
      maxMoves = Submarine.length + 5
    )
    res shouldBe Right(Sunk(targetShip))

  it should "attack randomly after a sunk" in:
    val bot               = createBotPlayer(AverageBotAttackStrategy())
    val (updatedBoard, _) = ShipAttack.attack(enemyBoard, G(1)) // to fake a hit attack for a bot
    val targetShip        = Frigate.verticalAt(G(1))
    val (boardWithSunk, _) = executeAttacksUntilSunk(
      bot,
      updatedBoard,
      targetShip,
      maxMoves = Frigate.length + 5
    )
    val (finalBoard, res) = bot.makeAttack(boardWithSunk)
    val adjacentPositions = List(F(1), H(1), G(2))
    val attackedPosition  = finalBoard.hits.diff(boardWithSunk.hits).headOption
    adjacentPositions should not contain attackedPosition

def executeAttacksUntilSunk(
    bot: Player,
    initialBoard: PlayerBoard,
    targetShip: Ship,
    maxMoves: Int
): (PlayerBoard, Either[String, AttackResult]) =

  @scala.annotation.tailrec
  def loop(currentBoard: PlayerBoard, movesLeft: Int): (PlayerBoard, Either[String, AttackResult]) =
    if (movesLeft <= 0) return (currentBoard, Left(s"Failed to sink ship within $maxMoves moves"))
    val (newBoard, result) = bot.makeAttack(currentBoard)
    result match
      case Right(Sunk(ship)) if ship == targetShip => (newBoard, result)
      case Right(Sunk(otherShip))                  => loop(newBoard, movesLeft - 1)
      case Right(_)                                => loop(newBoard, movesLeft - 1)
      case Left(error)                             => (currentBoard, Left(error))

  loop(initialBoard, maxMoves)
