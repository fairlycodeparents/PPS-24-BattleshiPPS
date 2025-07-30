package it.unibo.shipps.model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import it.unibo.shipps.model.PlayerFactory.*

class PlayerTest extends AnyFlatSpec with should.Matchers:
  "A human player" should "be created with its own attack strategy" in:
    val player = createHumanPlayer("player1")
    player.isABot shouldBe false
    player shouldBe HumanPlayer("player1", HumanAttackStrategy())

  "A bot player" should "be created with an attack strategy" in:
    val player = createBotPlayer(RandomBotAttackStrategy())
    player.isABot shouldBe true
    player shouldBe BotPlayer(RandomBotAttackStrategy())
