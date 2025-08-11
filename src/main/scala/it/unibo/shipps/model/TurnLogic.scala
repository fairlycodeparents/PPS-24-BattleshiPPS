package it.unibo.shipps.model

import it.unibo.shipps.model.player.Player

/** Represents the turn of the player in the game.
  * FirstPlayer: The first player is taking their turn.
  * SecondPlayer: The second player is taking their turn.
  */
enum Turn:
  case FirstPlayer, SecondPlayer

object TurnLogic:

  /** Determines if it's a bot turn
    * @param turn         current turn
    * @param firstPlayer  first player
    * @param secondPlayer second player
    * @return true if it's bot's turn
    */
  def isBotTurn(turn: Turn, firstPlayer: Player, secondPlayer: Player): Boolean =
    getCurrentPlayer(turn, firstPlayer, secondPlayer).isABot

  /** Gets the current player based on turn
    *
    * @param turn         current turn
    * @param firstPlayer  first player
    * @param secondPlayer second player
    * @return current player
    */
  def getCurrentPlayer(turn: Turn, firstPlayer: Player, secondPlayer: Player): Player =
    turn match
      case Turn.FirstPlayer  => firstPlayer
      case Turn.SecondPlayer => secondPlayer

  /** Switches to the next turn
    *
    * @param currentTurn current turn
    * @return next turn
    */
  def switchTurn(currentTurn: Turn): Turn =
    currentTurn match
      case Turn.FirstPlayer  => Turn.SecondPlayer
      case Turn.SecondPlayer => Turn.FirstPlayer
