# Random Generator Case Study

Scala case study about generating random values (similar to ScalaCheck's `Arbitrary` type class).
Based on [Noel Welsh's blog post](http://underscore.io/blog/posts/2016/06/27/opaque-transparent-interpreters.html).

Licensed [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0).

## Synopsis

`Gen` is a type class and an algebraic data type. Instances of `Gen[A]` produce random values of type `A`:

~~~scala
import random._

Gen[Double].sample
Gen[Double].sample
Gen[Double].sample
// res0: Double = 0.19561442200080503
// res1: Double = 0.1058008917669182
// res2: Double = 0.056512570402601936

Gen[Boolean].sample
Gen[Boolean].sample
Gen[Boolean].sample
// res3: Boolean = false
// res4: Boolean = true
// res5: Boolean = false

Gen[(Double, Int, Boolean)].sample
Gen[(Double, Int, Boolean)].sample
Gen[(Double, Int, Boolean)].sample
// res6: (Double, Int, Boolean) = (0.18952369749132558,-1383115028,true)
// res7: (Double, Int, Boolean) = (0.26882507588835747,1577710566,true)
// res8: (Double, Int, Boolean) = (0.3563608562773015,-675483119,true)
~~~

You can define new instances of `Gen` from existing ones using `map`, `flatMap`, and `zip`:

~~~scala
val genNone = Gen.constant(Option.empty[Double])
// genNone: random.Gen[Option[Double]] = ...

val genSome = Gen[Double] map (Option(_))
// genSome: random.Gen[Option[Double]] = ...

val genOption = for {
  empty  <- Gen[Boolean]
  option <- if(empty) genNone else genSome
} yield option
// genOption: random.Gen[Option[Double]] = ...

genOption.sample
genOption.sample
genOption.sample
// res9: Option[Double] = None
// res10: Option[Double] = Some(0.5740714831193248)
// res11: Option[Double] = Some(0.014157998121322124)

val genPoint = Gen[Double] zip Gen[Double] map (pair => Point(pair._1, pair._2))
// genPoint: random.Gen[random.Point] = ...

genPoint
genPoint
genPoint
// res12: random.Point = Point(0.26945230035571854,0.16615155966489625)
// res13: random.Point = Point(0.19340241204251418,0.9769430836757552)
// res14: random.Point = Point(0.1206411447870156,0.8545951874047162)
~~~
