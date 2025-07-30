package it.unibo.shipps.view.components

import it.unibo.shipps.Main.{listenTo, reactions}
import it.unibo.shipps.controller.{GameController, GameState}
import it.unibo.shipps.model.{PlayerBoard, Position}
import it.unibo.shipps.view.handler.{ClickHandler, ClickState}
import it.unibo.shipps.view.renderer.ButtonRenderer

import scala.swing.event.ButtonClicked
import scala.swing.{Button, Swing}

class GridManager(controller: GameController) {
  private var clickState = ClickState(None, 0L)

  def createButtons(state: GameState): IndexedSeq[Button] = {
    for {
      y <- 0 until PlayerBoard.size
      x <- 0 until PlayerBoard.size
    } yield {
      val pos = Position(x, y)
      val btn = ButtonFactory.createGridButton(pos, state)

      listenTo(btn)
      reactions += {
        case ButtonClicked(`btn`) =>
          val now                        = System.currentTimeMillis()
          val (newClickState, clickType) = ClickHandler.processClick(pos, now, clickState)
          clickState = newClickState
          ClickHandler.handleClick(clickType, controller)
      }
      btn
    }
  }
}

object ButtonFactory {
  def createGridButton(pos: Position, state: GameState): Button = {
    new Button(ButtonRenderer.getText(pos, state)) {
      opaque = true
      border = Swing.LineBorder(java.awt.Color.LIGHT_GRAY)
      background = ButtonRenderer.getColor(pos, state)
    }
  }

  def createStartGameButton(): Button = {
    new Button("Start Game") {
      background = java.awt.Color.GREEN
      foreground = java.awt.Color.BLACK
    }
  }
}
