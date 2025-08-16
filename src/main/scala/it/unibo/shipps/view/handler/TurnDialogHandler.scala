package it.unibo.shipps.view.handler

import it.unibo.shipps.model.Turn
import it.unibo.shipps.view.SimpleGui
import it.unibo.shipps.view.components.DialogFactory

import javax.swing.{JDialog, JLabel}

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

  /** Shows a dialog with the result of a bot action.
    * @param result the result message to display
    * @param onDismiss callback to execute when the dialog is closed
    */
  def showBotResultDialog(result: String, onDismiss: () => Unit): Unit =
    hideCurrentDialog()
    val dialog = DialogFactory.createBotResultDialog(gui, result, onDismiss)
    currentDialog = Some(dialog)
    DialogFactory.showDialog(dialog)
