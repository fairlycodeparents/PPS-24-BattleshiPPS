package it.unibo.shipps.model.board.exceptions

import it.unibo.shipps.model.board.Position

case class PositionOccupiedException(pos: Position) extends Exception(
      s"Position $pos is already occupied by one or more ships."
    )
