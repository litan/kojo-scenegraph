package net.kogics.kojo.scenegraph

import java.awt.Color

import net.kogics.kojo.scenegraph.Utils._

object TestDrawing {
  val noColor = new Color(0, 0, 0, 0)
  val size = 100

  def drawing0_1 = {
    val shape = PicShape.rect(size, size)
    Stroke(Color.yellow, Stack(Vector(
      Translate(-200, 0, Brighten(-0.5, Fill(new Color(0, 94, 0), shape))),
      Translate(0, 0, Brighten(0, Fill(new Color(0, 94, 0), shape))),
      Translate(200, 0, Brighten(0.5, Fill(new Color(0, 94, 0), shape)))
    )))
  }

  def drawing0_2 = {
    val shape = PicShape.circle(size/2)
    Stroke(Color.yellow, Stack(Vector(
      Translate(-200, 0, Brighten(-0.5, Fill(new Color(0, 94, 0), shape))),
      Translate(0, 0, Brighten(0, Fill(new Color(0, 94, 0), shape))),
      Translate(200, 0, Brighten(0.5, Fill(new Color(0, 94, 0), shape)))
    )))
  }

  def drawing1(n: Int) = {
    val S = PicShape.rect(size, 100)
    val stem = Scale(0.13, 1, Stroke(noColor, Fill(Color.black, S)))

    def drawing(n: Int): Picture = {
      if (n == 1)
        stem
      else
        Stack(Vector(stem,
          Translate(0, size - 5, Brighten(0.05, Stack(Vector(
            Rotate(25, Scale(0.72, 0.72, drawing(n - 1))),
            Rotate(-50, Scale(0.55, 0.55, drawing(n - 1)))
          )))))
        )
    }
    drawing(n)
  }

  def drawing2(n: Int) = {
    def sq(n: Double) = Stroke(noColor, Fill(Color.black, PicShape.rect(n, n)))

    def pattern(size: Double) = {
      if (randomDouble(1) < 0.1) pattern2(size) else pattern1(size)
    }

    def pattern1(size: Double): Picture = {
      if (size < 0.1) {
        sq(size)
      }
      else {
        Stack(Vector(
          sq(size),
          Translate(0, size * 1.1, Rotate(1.5, pattern(size * 0.99)))
        ))
      }
    }

    def pattern2(size: Double): Picture = {
      if (size < 0.1) {
        sq(size)
      }
      else {
        Stack(Vector(
          sq(size),
          Translate(0, size * 1.1, Rotate(1.5, pattern(size * 0.8))),
          Translate(size * 1, size * 1.1, Rotate(-60, pattern(size * 0.7))),
          Translate(-size * 1, size * 1.1, Rotate(60, pattern(size * 0.5)))
        ))
      }
    }
    pattern(n)
  }
}
