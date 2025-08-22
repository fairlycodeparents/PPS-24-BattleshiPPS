package it.unibo.shipps.controller.utils

import javax.swing.Timer

object DelayedExecutor:

  /** Executes an action after a delay.
    * @param delayMs milliseconds to wait before executing the action
    * @param action the code to execute
    */
  def runLater(delayMs: Int = 1000)(action: => Unit): Unit =
    Timer(delayMs)(action)

  private def Timer(delay: Int)(action: => Unit): Unit =
    val timer = new javax.swing.Timer(delay, _ => action)
    timer.setRepeats(false)
    timer.start()
