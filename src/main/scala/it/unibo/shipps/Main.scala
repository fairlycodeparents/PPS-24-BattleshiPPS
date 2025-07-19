package it.unibo.shipps

import it.unibo.shipps.model.*
import it.unibo.shipps.controller.*
import it.unibo.shipps.view.*

import scala.swing.{Frame, SimpleSwingApplication}

object Main extends SimpleSwingApplication:

  def top: Frame =
    val initialBoard                 = PlayerBoard()
    val positioning: ShipPositioning = ShipPositioningImpl
    val ships = List(
      ShipImpl(ShipType.Carrier, Position(0, 0), Orientation.Horizontal),
      ShipImpl(ShipType.Destroyer, Position(0, 0), Orientation.Horizontal),
      ShipImpl(ShipType.Submarine, Position(0, 0), Orientation.Horizontal),
      ShipImpl(ShipType.Frigate, Position(0, 0), Orientation.Horizontal)
    )
    val board = positioning.randomPositioning(initialBoard, ships)
      .getOrElse(initialBoard)
    val controller = new GameController(board, positioning, null)
    val view       = new SimpleGui(controller)
    controller.view = view
    view.update(board, None)
    view
