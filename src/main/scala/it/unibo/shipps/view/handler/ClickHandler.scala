package it.unibo.shipps.view.handler

import it.unibo.shipps.controller.GameController
import it.unibo.shipps.model.Position

/** Represents the type of click event. */
sealed trait ClickType
object ClickType {
  /** Represents a single click event. 
   * @param pos the position of the click
   */
  case class SingleClick(pos: Position) extends ClickType

  /** Represents a double click event.
   * @param pos the position of the click
   */
  case class DoubleClick(pos: Position) extends ClickType
}

/** Represents the state of a click event. 
 * @param pos the position of the click
 * @param time the time of the click
 */
case class ClickState(pos: Option[Position], time: Long)

/** Handles click events in the game. */
object ClickHandler {
  private val DOUBLE_CLICK_MILLIS = 500

  /** Processes a click event and determines if it is a single or double click.
    * @param pos the position of the click
    * @param currentTime the current time in milliseconds
    * @param previousClick the previous click state
    * @return a tuple containing the updated click state and the type of click
    */
  def processClick(
      pos: Position,
      currentTime: Long,
      previousClick: ClickState
  ): (ClickState, ClickType) = {

    val isDoubleClick = previousClick.pos.contains(pos) &&
      (currentTime - previousClick.time < DOUBLE_CLICK_MILLIS)

    if (isDoubleClick) {
      (ClickState(None, 0), ClickType.DoubleClick(pos))
    } else {
      (ClickState(Some(pos), currentTime), ClickType.SingleClick(pos))
    }
  }

  /** Handles the click event based on the type of click and the game controller.
    * @param clickType the type of click
    * @param controller the game controller to handle the click
    */
  def handleClick(clickType: ClickType, controller: GameController): Unit = {
    clickType match
      case ClickType.SingleClick(pos) => controller.onCellClick(pos)
      case ClickType.DoubleClick(pos) => controller.onCellDoubleClick(pos)
  }
}
