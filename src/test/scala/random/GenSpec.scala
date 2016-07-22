package random

import org.scalatest._

class GenSpec extends WordSpec with Matchers {
  val rng0 = Rng.constant(0.000000000000000)
  val rng1 = Rng.constant(0.999999999999999)
  val rng5 = Rng.constant(0.500000000000000)

  "Gen.pure" should {
    "uses the correct Rng" in {
      val gen = Gen.pure(rng => rng.sample)
      gen.sample(rng0) should be(0.000000000000000)
      gen.sample(rng1) should be(0.999999999999999)
      gen.sample(rng5) should be(0.500000000000000)
    }
  }

  "Gen.constant" should {
    "always produce the same value" in {
      val gen = Gen.constant("foo")
      gen.sample(rng0) should be("foo")
      gen.sample(rng1) should be("foo")
      gen.sample(rng5) should be("foo")
    }
  }

  "Gen.choose" should {
    "produce values from its entire source list" in {
      val gen = Gen.choose(List("foo", "bar", "baz"))
      gen.sample(rng0) should be("foo")
      gen.sample(rng1) should be("baz")
      gen.sample(rng5) should be("bar")
    }
  }

  "Gen.int(from, to)" should {
    "produce values from its entire source range" in {
      val gen = Gen.int(-2, 2)
      gen.sample(rng0) should be(-2)
      gen.sample(rng1) should be(2)
      gen.sample(rng5) should be(0)
    }
  }

  "Gen.int(to)" should {
    "produce values from its entire source range" in {
      val gen = Gen.int(2)
      gen.sample(rng0) should be(0)
      gen.sample(rng1) should be(2)
      gen.sample(rng5) should be(1)
    }
  }

  "Gen.int" should {
    "produce values from Int.MinValue to Int.MaxValue" in {
      val gen = Gen[Int]
      gen.sample(rng0) should be(Int.MinValue)
      gen.sample(rng1) should be(Int.MaxValue)
      gen.sample(rng5) should be(0)
    }
  }

  "Gen.option" should {
    "produce none and some values" in {
      val gen = Gen[Option[Int]]
      gen.sample(rng0) should be(None)
      gen.sample(rng1) should be(Some(Int.MaxValue))
      gen.sample(rng5) should be(None)
    }
  }

  "Gen.list(maxLength)" should {
    "produce none and some values" in {
      val gen = Gen.list[Int](2)
      gen.sample(rng0) should be(Nil)
      gen.sample(rng1) should be(List(Int.MaxValue, Int.MaxValue))
      gen.sample(rng5) should be(List(0))
    }
  }

  "Gen.tuple2" should {
    "produce tuples containing appropriate values" in {
      val gen = Gen[(Double, Int)]
      gen.sample(rng0) should be((0.000000000000000, Int.MinValue))
      gen.sample(rng1) should be((0.999999999999999, Int.MaxValue))
      gen.sample(rng5) should be((0.500000000000000, 0))
    }
  }

  "Gen.tuple3" should {
    "produce tuples containing appropriate values" in {
      val gen = Gen[(Double, Int, Boolean)]
      gen.sample(rng0) should be((0.000000000000000, Int.MinValue, false))
      gen.sample(rng1) should be((0.999999999999999, Int.MaxValue, true))
      gen.sample(rng5) should be((0.500000000000000, 0, false))
    }
  }

  "Gen.point" should {
    "produce points containing appropriate values" in {
      val gen = Gen[Point]
      gen.sample(rng0) should be(Point(0.000000000000000, 0.000000000000000))
      gen.sample(rng1) should be(Point(0.999999999999999, 0.999999999999999))
      gen.sample(rng5) should be(Point(0.500000000000000, 0.500000000000000))
    }
  }
}
