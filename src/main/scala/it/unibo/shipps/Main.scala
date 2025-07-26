package it.unibo.shipps

import it.unibo.shipps.model.*
import it.unibo.shipps.model.ShipType.*
import it.unibo.shipps.controller.*
import it.unibo.shipps.view.*

import scala.swing.{Frame, SimpleSwingApplication}

object Main extends SimpleSwingApplication:

  def top: Frame =
    val initialBoard                 = PlayerBoard()
    val positioning: ShipPositioning = ShipPositioningImpl
    val ships = List(
      Carrier.horizontalAt(0, 0),
      Destroyer.horizontalAt(0, 0),
      Submarine.horizontalAt(0, 0),
      Frigate.horizontalAt(0, 0)
    )
    val board = positioning.randomPositioning(initialBoard, ships)
      .getOrElse(initialBoard)
    val controller = new GameController(board, board, positioning, null)
    val view       = new SimpleGui(controller)
    controller.view = view
    view.update(board, None)
    view
