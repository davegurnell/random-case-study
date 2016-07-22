package random

/**
 * Source of randomness used by [[Gen]] objects.
 *
 * There are two implementations of [[Rng]]:
 *
 * - [[RandomRng]] uses Scala's random number generator to produce `Doubles` between 0 and 1;
 * - [[ConstantRng]] is a fake [[Rng]] that always produces the same value (useful for testing).
 */
sealed trait Rng {
  def sample: Double = this match {
    case RandomRng          => scala.util.Random.nextDouble
    case ConstantRng(value) => value
  }
}

/**
 * Implementation of [[Rng]] based on [[scala.util.Random]].
 */
final case object RandomRng extends Rng

/**
 * Fake implementation of [[Rng]] that alwaus produces `number`.
 * Useful for writing unit tests for [[Gen]].
 */
final case class ConstantRng(number: Double) extends Rng

object Rng {
  /**
   * Create a [[RandomRng]].
   */
  implicit val random: Rng =
    RandomRng

  /**
   * Create a [[ConstantRng]] that always produces `number`.
   */
  def constant(number: Double): Rng =
    ConstantRng(number)
}
