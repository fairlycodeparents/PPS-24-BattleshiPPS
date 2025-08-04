package it.unibo.shipps

import it.unibo.shipps.controller.GameSetup

import scala.swing.{Frame, MainFrame, SimpleSwingApplication}

object Main extends SimpleSwingApplication:

  override def top: Frame =
    val frame = new MainFrame()
    new GameSetup(frame)
    frame
