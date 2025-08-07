package it.unibo.shipps.model.board

import org.scalatest.*
import flatspec.*
import matchers.*

/** Test suite for [[Position]]. */
class PositionTest extends AnyFlatSpec with should.Matchers:

  it should "create a horizontal range of positions when given a Range and an Int" in:
    Position(1 to 4, 2) shouldEqual Set(
      Position(1, 2),
      Position(2, 2),
      Position(3, 2),
      Position(4, 2)
    )

  it should "create a vertical range of positions when given an Int and a Range" in:
    Position(5, 6 to 8) shouldEqual Set(
      Position(5, 6),
      Position(5, 7),
      Position(5, 8)
    )

  it should "create a grid of positions when given two Ranges" in:
    Position(1 to 2, 3 to 4) shouldEqual Set(
      Position(1, 3),
      Position(1, 4),
      Position(2, 3),
      Position(2, 4)
    )

  it should "handle an empty range gracefully" in:
    Position(5 to 2, 1) shouldEqual Set.empty
