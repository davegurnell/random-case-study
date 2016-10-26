package random

object Main extends App {
  Canvas.draw("Even spread", 1000, Rng.random)(Gen[Point])

  Canvas.draw("Triangle", 1000, Rng.random) {
    for {
      x <- Gen[Double]
      y <- Gen[Double]
    } yield Point(x, .5 + x * (y - .5))
  }

  Canvas.draw("Circle", 1000, Rng.random) {
    for {
      a <- Gen[Double] map (_ * 2 * math.Pi)
      r <- Gen[Double] map (_ * .5)
    } yield Point(
      .5 + r * math.cos(a),
      .5 + r * math.sin(a)
    )
  }
}