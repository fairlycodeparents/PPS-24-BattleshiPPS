package it.unibo.shipps.model.board

/** A type alias to represent a coordinate dimension, which can be a single Int
  * or a Range of Ints.
  */
type CoordinateDim = Int | Range

/** Represents a position on a 2D grid, defined by its column and row coordinates.
  * @param col the column coordinate
  * @param row the row coordinate
  */
case class Position(col: Int, row: Int)

object Position:
  /** Creates a Set of Position objects from the defined column and row dimensions.
    * @param colDim The column dimension, as a single Int or a Range.
    * @param rowDim The row dimension, as a single Int or a Range.
    * @return A Set containing all the positions within the range.
    */
  def apply(colDim: CoordinateDim, rowDim: CoordinateDim): Set[Position] =
    val (cols, rows) = (colDim, rowDim) match
      case (r1: Range, r2: Range) => (r1, r2)
      case (r1: Range, i2: Int)   => (r1, i2 to i2)
      case (i1: Int, r2: Range)   => (i1 to i1, r2)
      case (i1: Int, i2: Int)     => (i1 to i1, i2 to i2)

    (for
      col <- cols
      row <- rows
    yield Position(col, row)).toSet
