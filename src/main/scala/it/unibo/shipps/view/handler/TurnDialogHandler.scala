package it.unibo.shipps.view.handler

import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.components.DialogFactory

import javax.swing.JDialog

class TurnDialogHandler(gui: SimpleGui):
  private var currentDialog: Option[JDialog] = None

  def showTurnDialog(playerName: String): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createTurnDialog(gui, playerName)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  def showWaitingDialog(): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createWaitingDialog(gui)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  def hideCurrentDialog(): Unit =
    DialogFactory.hideDialogOpt(currentDialog)
    currentDialog = None
