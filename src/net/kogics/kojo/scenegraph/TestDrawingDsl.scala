/*
 * Copyright (C) 2015 Lalit Pant <pant.lalit@gmail.com>
 *
 * The contents of this file are subject to the GNU General Public License
 * Version 3 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.gnu.org/copyleft/gpl.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 */

package net.kogics.kojo.scenegraph

import java.awt.Color

import net.kogics.kojo.scenegraph.Utils._

object TestDrawingDsl {
  val noColor = new Color(0, 0, 0, 0)
  val size = 100

  def drawing0_0 = {
    val shape = PicShape.rect(size, size)
    //    val shape = PicShape.image("abc.png")
    penColor(Color.yellow) * trans(-200, 0) -> shape
    //    penColor(Color.yellow) -> (trans(-200, 0) -> shape)
  }

  def drawing0_1 = {
    val shape = PicShape.rect(size, size)
    penColor(Color.yellow) -> PicStack(
      trans(-200, 0) * brit(-0.5) * fillColor(new Color(0, 94, 0)) -> shape,
      trans(0, 0) * brit(0) * fillColor(new Color(0, 94, 0)) -> shape,
      trans(200, 0) * brit(0.5) * fillColor(new Color(0, 94, 0)) -> shape
    )
  }

  def drawing0_2 = {
    val shape = PicShape.circle(size / 2)
    penColor(Color.yellow) -> PicStack(
      trans(-200, 0) * brit(-0.5) * fillColor(new Color(0, 94, 0)) -> shape,
      trans(0, 0) * brit(0) * fillColor(new Color(0, 94, 0)) -> shape,
      trans(200, 0) * brit(0.5) * fillColor(new Color(0, 94, 0)) -> shape
    )
  }

  def drawing1(n: Int) = {
    val S = PicShape.rect(size, 100)
    val stem = scale(0.13, 1) * penColor(noColor) * fillColor(Color.black) -> S

    def drawing(n: Int): VectorPicture = {
      if (n == 1)
        stem
      else
        PicStack(
          stem,
          trans(0, size - 5) * brit(0.05) -> PicStack(
            rot(25) * scale(0.72, 0.72) -> drawing(n - 1),
            rot(-50) * scale(0.55, 0.550) -> drawing(n - 1)
          )
        )
    }
    drawing(n)
  }

  def drawing2(n: Int) = {
    def sq(n: Double) = penColor(noColor) * fillColor(Color.black) -> PicShape.rect(n, n)

    def pattern(size: Double) = {
      if (randomDouble(1) < 0.1) pattern2(size) else pattern1(size)
    }

    def pattern1(size: Double): VectorPicture = {
      if (size < 0.1) {
        sq(size)
      }
      else {
        PicStack(
          sq(size),
          trans(size * 0, size * 1.2) * rot(1.5) -> pattern(size * 0.99)
        )
      }
    }

    def pattern2(size: Double): VectorPicture = {
      if (size < 0.1) {
        sq(size)
      }
      else {
        PicStack(
          sq(size),
          trans(size * 0, size * 1.2) * rot(1.5) -> pattern(size * 0.8),
          trans(size * .1, size * 1.2) * rot(-60) -> pattern(size * 0.7),
          trans(-size * .1, size * 1.2) * rot(60) -> pattern(size * 0.5)
        )
      }
    }
    pattern(n)
  }
}
