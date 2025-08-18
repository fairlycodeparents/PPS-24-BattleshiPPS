package it.unibo.shipps.logic

import it.unibo.shipps.controller.{GamePhase, GameState}
import it.unibo.shipps.model.board.{PlayerBoard, Position}
import it.unibo.shipps.model.player.{BotPlayer, HumanPlayer, Player}
import it.unibo.shipps.model.ShipOrientation.{Horizontal, Vertical}
import it.unibo.shipps.model.{
  AttackResult,
  BattleLogic,
  HumanAttackStrategy,
  RandomBotAttackStrategy,
  Ship,
  ShipType,
  Turn
}
import it.unibo.shipps.view.renderer.ColorScheme
import org.scalatest.*
import flatspec.*
import matchers.*

/** Test suite for the BattleLogic object. */
class BattleLogicTest extends AnyFlatSpec with should.Matchers:

  private val testPosition  = Position(2, 3)
  private val testShip      = ShipType.Frigate.at(testPosition, Horizontal)
  private val emptyBoard    = PlayerBoard()
  private val boardWithShip = emptyBoard.addShip(testShip).getOrElse(fail("Failed to add ship"))

  private val humanPlayer = HumanPlayer("TestPlayer", HumanAttackStrategy())
  private val botPlayer   = BotPlayer(RandomBotAttackStrategy())

  private val initialGameState = GameState(
    board = boardWithShip,
    enemyBoard = boardWithShip,
    selectedShip = None,
    gamePhase = GamePhase.Battle
  )

  "BattleLogic.processBattleClick" should "handle human player miss attack correctly" in:
    val emptyEnemyBoard = PlayerBoard()
    val gameState       = initialGameState.copy(enemyBoard = emptyEnemyBoard)
    val missPosition    = Position(0, 0)

    val result = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(missPosition)
    )

    result.messages should have size 1
    result.messages.head should include("Miss")
    result.newState.attackResult should contain key missPosition
    result.newState.attackResult(missPosition) shouldBe AttackResult.Miss
    result.newState.cellColors(missPosition) shouldBe ColorScheme.MISS

  it should "handle human player hit attack correctly" in:
    val gameState   = initialGameState
    val hitPosition = testPosition

    val result = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(hitPosition)
    )

    result.messages should have size 1
    result.messages.head should include("Hit")
    result.newState.attackResult should contain key hitPosition
    result.newState.attackResult(hitPosition) match
      case AttackResult.Hit(ship) => ship shouldBe testShip
      case _                      => fail("Expected Hit result")
    result.newState.cellColors(hitPosition) shouldBe ColorScheme.HIT

  it should "handle human player sunk ship correctly" in:
    val frigate          = ShipType.Frigate.at(Position(0, 0), Horizontal)
    val positions        = List(Position(0, 0), Position(1, 0))
    val boardWithFrigate = emptyBoard.addShip(frigate).getOrElse(fail())
    val gameState        = initialGameState.copy(enemyBoard = boardWithFrigate)

    val finalResult = positions.foldLeft((gameState, List.empty[String])) {
      case ((currentState, messages), pos) =>
        val result = BattleLogic.processBattleClick(
          currentState,
          humanPlayer,
          Turn.FirstPlayer,
          Some(pos)
        )
        (result.newState, messages ++ result.messages)
    }

    val (finalState, allMessages) = finalResult
    frigate.positions.foreach { pos =>
      finalState.cellColors(pos) shouldBe ColorScheme.SUNK
    }

  it should "handle already attacked position correctly" in:
    val gameState        = initialGameState
    val attackedPosition = testPosition

    val firstResult = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(attackedPosition)
    )

    val secondResult = BattleLogic.processBattleClick(
      firstResult.newState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(attackedPosition)
    )

    secondResult.messages should have size 1
    secondResult.messages.head should include("already attacked")

  it should "handle human player without position correctly" in:
    val result = BattleLogic.processBattleClick(
      initialGameState,
      humanPlayer,
      Turn.FirstPlayer,
      None
    )

    result.messages should have size 1
    result.messages.head should include("Position required")

  it should "handle bot player attack correctly" in:
    val gameState = initialGameState

    val result = BattleLogic.processBattleClick(
      gameState,
      botPlayer,
      Turn.SecondPlayer,
      None
    )

    result.messages should have size 1
    result.messages.head should (include("Miss") or include("Hit"))

    val originalHits = gameState.board.hits
    val newHits      = result.newState.board.hits
    newHits should have size (originalHits.size + 1)

  it should "handle second player attacks on first player board correctly" in:
    val gameState      = initialGameState
    val attackPosition = testPosition

    val result = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.SecondPlayer,
      Some(attackPosition)
    )

    result.newState.enemyAttackResult should contain key attackPosition
    result.newState.enemyCellColors should contain key attackPosition

  it should "handle game over scenario correctly" in:
    val singleShip          = ShipType.Frigate.at(Position(0, 0), Horizontal)
    val positions           = List(Position(0, 0), Position(1, 0))
    val boardWithSingleShip = emptyBoard.addShip(singleShip).getOrElse(fail())
    val gameState = initialGameState.copy(
      enemyBoard = boardWithSingleShip,
      gamePhase = GamePhase.Battle
    )

    val finalResult = positions.foldLeft((gameState, List.empty[String])) {
      case ((currentState, messages), pos) =>
        val result = BattleLogic.processBattleClick(
          currentState,
          humanPlayer,
          Turn.FirstPlayer,
          Some(pos)
        )
        (result.newState, messages ++ result.messages)
    }

    val (finalState, allMessages) = finalResult
    finalState.gamePhase shouldBe GamePhase.GameOver

  "BattleLogic" should "maintain immutability of input state" in:
    val originalState        = initialGameState
    val originalBoard        = originalState.enemyBoard
    val originalAttackResult = originalState.attackResult
    val originalCellColors   = originalState.cellColors

    BattleLogic.processBattleClick(
      originalState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(Position(0, 0))
    )

    originalState.enemyBoard shouldBe originalBoard
    originalState.attackResult shouldBe originalAttackResult
    originalState.cellColors shouldBe originalCellColors
    originalState.gamePhase shouldBe GamePhase.Battle

  it should "handle multiple consecutive attacks correctly" in:
    val gameState = initialGameState.copy(enemyBoard = emptyBoard)
    val positions = List(Position(0, 0), Position(1, 1), Position(2, 2))

    val finalResult = positions.foldLeft((gameState, List.empty[String])) {
      case ((currentState, messages), pos) =>
        val result = BattleLogic.processBattleClick(
          currentState,
          humanPlayer,
          Turn.FirstPlayer,
          Some(pos)
        )
        (result.newState, messages ++ result.messages)
    }

    val (finalState, allMessages) = finalResult

    allMessages should have size 3
    allMessages.foreach(_ should include("Miss"))
    finalState.attackResult should have size 3
    positions.foreach { pos =>
      finalState.attackResult should contain key pos
      finalState.cellColors should contain key pos
    }

  it should "correctly differentiate between first and second player attacks" in:
    val gameState      = initialGameState
    val attackPosition = Position(0, 0)

    val firstPlayerResult = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.FirstPlayer,
      Some(attackPosition)
    )

    val secondPlayerResult = BattleLogic.processBattleClick(
      gameState,
      humanPlayer,
      Turn.SecondPlayer,
      Some(attackPosition)
    )

    firstPlayerResult.newState.attackResult should contain key attackPosition
    firstPlayerResult.newState.enemyAttackResult shouldBe empty

    secondPlayerResult.newState.enemyAttackResult should contain key attackPosition
    secondPlayerResult.newState.attackResult shouldBe empty
