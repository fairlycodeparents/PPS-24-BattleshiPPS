package it.unibo.shipps.model

/**
 * Represents a position on a 2D grid, defined by its x and y coordinates.
 */
trait Position:
  /**
   * Returns the x coordinate of the position.
   * @return the x coordinate
   */
  def x(): Int

  /**
   * Returns the y coordinate of the position.
   * @return the y coordinate
   */
  def y(): Int
