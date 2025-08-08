package it.unibo.shipps.view.components

import it.unibo.shipps.view.SimpleGui

import java.awt.BorderLayout
import javax.swing.{JDialog, JLabel}
import scala.swing.Swing

/** Factory object for creating dialogs. */
object DialogFactory:

  private def hideDialog(dialog: JDialog): Unit =
    Swing.onEDT {
      dialog.setVisible(false)
      dialog.dispose()
    }

  /** Creates and shows a turn dialog
    * @param view the main GUI view
    * @param playerName name of the player whose turn it is
    * @return the created dialog for external management
    */
  def createTurnDialog(view: SimpleGui, playerName: String): JDialog =
    val dialog = new JDialog(view.peer, "Player Turn", true)
    dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    dialog.setSize(350, 200)
    dialog.setLocationRelativeTo(view.peer)
    dialog.setResizable(false)

    val label = new JLabel(s"<html><center>It's $playerName's turn<br><br>Click OK when ready</center></html>")
    label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

    val okButton = new javax.swing.JButton("OK")
    okButton.addActionListener(_ => {
      dialog.setVisible(false)
      dialog.dispose()
    })

    val panel = new javax.swing.JPanel(new BorderLayout())
    panel.add(label, BorderLayout.CENTER)
    panel.add(okButton, BorderLayout.SOUTH)

    dialog.add(panel)
    dialog

  /** Creates and shows a waiting dialog
    * @param view the main GUI view
    * @return the created dialog for external management
    */
  def createWaitingDialog(view: SimpleGui): JDialog =
    val dialog = new JDialog(view.peer, "Bot Turn", true)
    dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    dialog.setSize(300, 150)
    dialog.setLocationRelativeTo(view.peer)
    dialog.setResizable(false)

    val label = new JLabel("<html><center>Bot is thinking...<br>Please wait</center></html>")
    label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

    dialog.add(label, BorderLayout.CENTER)
    dialog

  /** Creates and shows a retry attack dialog
    * @param view the main GUI view
    * @return the created dialog for external management
    */
  def createRetryAttackDialog(view: SimpleGui): JDialog =
    val dialog = new JDialog(view.peer, "Retry Attack", true)
    dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE)
    dialog.setSize(300, 150)
    dialog.setLocationRelativeTo(view.peer)
    dialog.setResizable(false)

    val label = new JLabel("<html><center>Invalid attack position<br>Please try again</center></html>")
    label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER)

    val okButton = new javax.swing.JButton("OK")
    okButton.addActionListener(_ => {
      dialog.setVisible(false)
      dialog.dispose()
    })

    val panel = new javax.swing.JPanel(new BorderLayout())
    panel.add(label, BorderLayout.CENTER)
    panel.add(okButton, BorderLayout.SOUTH)

    dialog.add(panel)
    dialog

  /** Shows a dialog on the EDT
    * @param dialog the dialog to show
    */
  def showDialog(dialog: JDialog): Unit =
    Swing.onEDT(dialog.setVisible(true))

  /** Hides an optional dialog
    * @param dialogOpt optional dialog to hide
    */
  def hideDialogOpt(dialogOpt: Option[JDialog]): Unit =
    dialogOpt.foreach(hideDialog)
