package it.unibo.shipps.logic

import it.unibo.shipps.model.player.PlayerFactory.{createBotPlayer, createHumanPlayer}
import it.unibo.shipps.model.{RandomBotAttackStrategy, Turn, TurnLogic}
import it.unibo.shipps.model.player.Player
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class TurnLogicTest extends AnyFlatSpec with Matchers:
  val humanPlayer: Player = createHumanPlayer("Human player")
  val botPlayer: Player   = createBotPlayer()

  "TurnLogic.isBotTurn" should "return true when current player is a bot" in:
    TurnLogic.isBotTurn(Turn.FirstPlayer, botPlayer, humanPlayer) shouldBe true
    TurnLogic.isBotTurn(Turn.SecondPlayer, humanPlayer, botPlayer) shouldBe true

  it should "return false when current player is human" in:
    TurnLogic.isBotTurn(Turn.FirstPlayer, humanPlayer, botPlayer) shouldBe false
    TurnLogic.isBotTurn(Turn.SecondPlayer, botPlayer, humanPlayer) shouldBe false

  "TurnLogic.getCurrentPlayer" should "return first player when turn is FirstPlayer" in:
    TurnLogic.getCurrentPlayer(Turn.FirstPlayer, humanPlayer, botPlayer) shouldBe humanPlayer

  it should "return second player when turn is SecondPlayer" in:
    TurnLogic.getCurrentPlayer(Turn.SecondPlayer, humanPlayer, botPlayer) shouldBe botPlayer

  "TurnLogic.switchTurn" should "switch from FirstPlayer to SecondPlayer" in:
    TurnLogic.switchTurn(Turn.FirstPlayer) shouldBe Turn.SecondPlayer

  it should "switch from SecondPlayer to FirstPlayer" in:
    TurnLogic.switchTurn(Turn.SecondPlayer) shouldBe Turn.FirstPlayer
