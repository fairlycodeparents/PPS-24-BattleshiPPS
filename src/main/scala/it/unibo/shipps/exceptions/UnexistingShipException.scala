package it.unibo.shipps.exceptions

case class UnexistingShipException() extends Exception("The ship does not exist on the player board.")
