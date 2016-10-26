package random

/**
 * Algebraic data type for objects that generate random values.
 *
 * @tparam A the type of value generated.
 */
sealed trait Gen[A] {

  /**
   * Generate a random value of type `A`.
   *
   * @param rng a number generator to use as a source of randomness.
   */
  def sample(rng: Rng): A = this match {
    case PureGen(func)            => func(rng)
    case MapGen(source, func)     => func(source.sample(rng))
    case FlatMapGen(source, func) => func(source.sample(rng)).sample(rng)
  }

  /**
   * Create a [[Gen]] for values of type `B`,
   * that produces values by sampling this [[Gen]]
   * and transforming the results with `func`.
   */
  def map[B](func: A => B): Gen[B] = {
    MapGen(this, func)
  }

  /**
   * Create a [[Gen]] that produces values of type `B`,
   * that produces values by sampling this [[Gen]],
   * creating a new [[Gen]] using `func`, and sampling that [[Gen]].
   *
   * For example, we can generate a mixture of integers and doubles like this:
   *
   * ```
   * Gen.boolean.flatMap { flag =>
   *   if(flag) Gen.int else Gen.double
   * }
   * ```
   */
  def flatMap[B](func: A => Gen[B]): Gen[B] = {
    FlatMapGen(this, func)
  }

  /**
   * Create a [[Gen]] that produces tuples of type `(A, B)`
   * by sampling `this` and `that` and zipping the results.
   */
  def zip[B](that: Gen[B]): Gen[(A, B)] = {
    for {
      a <- this
      b <- that
    } yield (a, b)
  }
}

/**
 * A basic [[Gen]] that produces values from a [[func]].
 */
final case class PureGen[A](func: Rng => A) extends Gen[A]

/**
 * A [[Gen]] that samples from a `source` [[Gen]]
 * and transforms the results with a mapping `func`.
 */
final case class MapGen[A, B](source: Gen[A], func: A => B) extends Gen[B]

/**
 * A [[Gen]] that samples from a `source` [[Gen]],
 * uses a `func` to create a second [[Gen]],
 * and then samples that [[Gen]].
 */
final case class FlatMapGen[A, B](source: Gen[A], func: A => Gen[B]) extends Gen[B]

/**
 * Constructors and implicit instances for [[Gen]].
 */
object Gen {
  /**
   * Summon an implicit instance of [[Gen]]. Use case:
   *
   * ```
   * val doubleGen: Gen[Double] = Gen.apply[Double]
   * ```
   */
  def apply[A](implicit gen: Gen[A]): Gen[A] = {
    gen
  }

  /**
   * Basic constructor: create a [[Gen]] from a `func`.
   */
  def pure[A](func: Rng => A): Gen[A] = {
    PureGen(func)
  }

  /**
   * Create a [[Gen]] that always produces `value`
   * (regardless of the random number generator).
   */
  def constant[A](value: A): Gen[A] = {
    Gen.pure(rng => value)
  }

  /**
   * Create a [[Gen]] that produces random items from `values`.
   */
  def choose[A](values: List[A]): Gen[A] = {
    Gen.pure(rng => values((values.length * rng.sample).toInt))
  }

  /**
   * [[Gen]] that produces random `Double` values
   * between `0` (inclusive) and `1` (exclusive).
   * Can be summoned implicitly:
   *
   * ```
   * Gen[Double]
   * ```
   */
  implicit val double: Gen[Double] = {
    PureGen(rng => rng.sample)
  }

  /**
   * Create a [[Gen]] that produces random `Int` values
   * between `from` (inclusive) and `to` (exclusive).
   */
  def int(from: Int, to: Int): Gen[Int] = {
    Gen[Double].map(n => ((n * (1.0 * to + 1 - from)) + from).toInt)
  }

  /**
   * Create a [[Gen]] that produces random `Int` values
   * between `0` (inclusive) and `to` (exclusive).
   */
  def int(to: Int): Gen[Int] = {
    int(0, to)
  }

  /**
   * [[Gen]] that produces random `Int` values.
   * Can be summoned implicitly:
   *
   * ```
   * Gen[Int]
   * ```
   */
  implicit val int: Gen[Int] = {
    int(Int.MinValue, Int.MaxValue)
  }

  /**
   * [[Gen]] that produces random `Boolean` values.
   * Can be summoned implicitly:
   *
   * ```
   * Gen[Boolean]
   * ```
   */
  implicit val boolean: Gen[Boolean] = {
    Gen[Double].map(n => n > 0.5)
  }

  /**
   * Given a `Gen[A]`, create a `Gen[Option[A]`.
   * Can be used to summon implicit [[Gen]] objects:
   *
   * ```
   * Gen[Option[Int]]
   * ```
   */
  implicit def option[A](implicit a: Gen[A]): Gen[Option[A]] = {
    val someGen = a.map(Option.apply[A])
    val noneGen = Gen.constant(Option.empty[A])
    Gen[Boolean].flatMap { nonEmpty =>
      if(nonEmpty) someGen else noneGen
    }
  }

  /**
   * Given a `Gen[A]`, create a `Gen[List[A]]`
   * that produces lists of length `0` (inclusive) to `maxLength` (exclusive).
   */
  def list[A](maxLength: Int)(implicit a: Gen[A]): Gen[List[A]] =
    for {
      len <- Gen.int(0, maxLength)
      lis <- Gen.pure(rng => (1 to len).map(_ => a.sample(rng)).toList)
    } yield lis

  /**
   * Given a `Gen[A]` and a `Gen[B]`, create a `Gen[(A, B)]`.
   * Can be used to summon implicit [[Gen]] objects:
   *
   * ```
   * Gen[(Int, Double)]
   * ```
   */
  implicit def tuple2[A, B](implicit a: Gen[A], b: Gen[B]): Gen[(A, B)] =
    for {
      a <- a
      b <- b
    } yield (a, b)

  /**
   * Given a `Gen[A]`, a `Gen[B]`, and a `Gen[C]`, create a `Gen[(A, B, C)]`.
   * Can be used to summon implicit [[Gen]] objects:
   *
   * ```
   * Gen[(Int, Double)]
   * ```
   */
  implicit def tuple3[A, B, C](implicit a: Gen[A], b: Gen[B], c: Gen[C]): Gen[(A, B, C)] =
    for {
      a <- a
      b <- b
      c <- c
    } yield (a, b, c)
}
