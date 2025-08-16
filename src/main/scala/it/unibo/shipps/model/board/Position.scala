package it.unibo.shipps.model.board

/** A type alias to represent a coordinate dimension, which can be a single Int or a Range of Int. */
type CoordinateDimension = Int | Range

/** Represents a position on a 2D grid, defined by its column and row coordinates.
  * @param col the column coordinate
  * @param row the row coordinate
  */
case class Position(col: Int, row: Int):

  /** Calculates the distance to another position using the Manhattan distance formula.
    * @param other the other [[Position]] to calculate the distance to
    * @return the Manhattan distance as an [[Int]]
    */
  def distanceTo(other: Position): Int =
    Math.abs(col - other.col) + Math.abs(row - other.row)

/** Companion object for the Position class, providing flexible factory methods. */
object Position:

  /** Creates a Set of Position objects from the defined column and row dimensions.
    * @param colDimension The columns dimension, as a single [[Int]] value or a [[Range]].
    * @param rowDimension The rows dimension, as a single [[Int]] value or a [[Range]].
    * @return A [[Set]] containing all the [[Position]] within the specified range.
    */
  def apply(colDimension: CoordinateDimension, rowDimension: CoordinateDimension): Set[Position] =
    val (colRange, rowRange) = (colDimension, rowDimension) match
      case (range1: Range, range2: Range) => (range1, range2)
      case (range1: Range, value2: Int)   => (range1, value2 to value2)
      case (value1: Int, range2: Range)   => (value1 to value1, range2)
      case (value1: Int, value2: Int)     => (value1 to value1, value2 to value2)

    (for
      col <- colRange
      row <- rowRange
    yield Position(col, row)).toSet
