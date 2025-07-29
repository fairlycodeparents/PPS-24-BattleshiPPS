package it.unibo.shipps.view.handler

import it.unibo.shipps.controller.GameController
import it.unibo.shipps.model.Position

sealed trait ClickType
object ClickType {
  case class SingleClick(pos: Position) extends ClickType

  case class DoubleClick(pos: Position) extends ClickType
}

case class ClickState(pos: Option[Position], time: Long)

object ClickHandler {
  private val DOUBLE_CLICK_MILLIS = 500

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

  def handleClick(clickType: ClickType, controller: GameController): Unit = {
    clickType match
      case ClickType.SingleClick(pos) => controller.onCellClick(pos)
      case ClickType.DoubleClick(pos) => controller.onCellDoubleClick(pos)
  }
}
