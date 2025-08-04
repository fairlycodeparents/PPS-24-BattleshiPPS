package it.unibo.shipps.model.board.exceptions

case class UnexistingShipException() extends Exception("The ship does not exist on the player board.")
