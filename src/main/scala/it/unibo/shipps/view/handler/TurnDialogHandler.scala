package it.unibo.shipps.view.handler

import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.components.DialogFactory

import javax.swing.JDialog

class TurnDialogHandler(gui: SimpleGui):
  private var currentDialog: Option[JDialog] = None

  /** Shows a dialog indicating whose turn it is.
    * @param playerName the name of the player whose turn it is
    */
  def showTurnDialog(playerName: String): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createTurnDialog(gui, playerName)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  /** Shows a waiting dialog while the game is processing an action. */
  def showWaitingDialog(): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createWaitingDialog(gui)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)

  /** Hides the currently displayed dialog. */
  def hideCurrentDialog(): Unit =
    DialogFactory.hideDialogOpt(currentDialog)
    currentDialog = None

  /** Shows a dialog to retry an attack after a failed attempt. */
  def retryAttack(): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createRetryAttackDialog(gui)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)
