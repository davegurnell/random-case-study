package random

import java.awt._
import javax.swing._

/**
 * Helpers for drawing scatterplots of random [[Point]] values.
 */
object Canvas {
  /**
   * Display a `JFrame` showing `num` random [[Point]] values produced by `gen`.
   */
  def draw(title: String, num: Int)(gen: Gen[Point])(implicit rng: Rng): Unit = {
    val frame = new JFrame(title)
    val panel = new JPanel {
      override def paintComponent(g: Graphics): Unit = g match {
        case g: Graphics2D =>
          val size = this.getSize()

          (1 to num).foreach { i =>
            val point = gen.sample
            val x = (size.getWidth * point.x).toInt
            val y = (size.getHeight * point.y).toInt
            g.fillRect(x-1, y-1, 3, 3)
          }
      }
    }

    frame.getContentPane.add(panel)
    frame.setSize(500, 500)
    frame.setVisible(true)
  }
}
