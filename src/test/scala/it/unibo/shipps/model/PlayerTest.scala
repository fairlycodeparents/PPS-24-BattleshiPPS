package it.unibo.shipps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import it.unibo.shipps.model.PlayerFactory.*

class PlayerTest extends AnyFlatSpec with should.Matchers:
  "A human player" should "be created" in:
    val player = createHumanPlayer("player1")
    player.isABot shouldBe false

  "A bot player" should "be created" in:
    val player = createBotPlayer
    player.isABot shouldBe true
