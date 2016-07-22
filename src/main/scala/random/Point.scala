package random

/**
 * An `(x, y)` point.
 */
case class Point(x: Double, y: Double)

object Point {
  /**
   * Create a [[Point]] from a pair of `Doubles`.
   */
  def tupled(pair: (Double, Double)): Point = {
    val (x, y) = pair
    Point(x, y)
  }

  /**
   * [[Gen]] that produces random [[Point]] values.
   */
  implicit val point: Gen[Point] =
    (Gen[Double] zip Gen[Double]) map (Point.tupled)
}
