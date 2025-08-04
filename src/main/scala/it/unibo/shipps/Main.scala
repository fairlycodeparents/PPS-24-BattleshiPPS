package it.unibo.shipps

import it.unibo.shipps.view.*

import scala.swing.{Frame, SimpleSwingApplication}

object Main extends SimpleSwingApplication:

  override def top: Frame = GameSetupFrame.top
