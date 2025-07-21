package it.unibo.shipps.exceptions

import it.unibo.shipps.model.Position

case class PositionOccupiedException(pos: Position) extends Exception(
      s"Position $pos is already occupied by one or more ships."
    )
