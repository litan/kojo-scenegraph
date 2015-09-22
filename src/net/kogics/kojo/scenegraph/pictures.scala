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

object PicShape {
  def rect(h: Double, w: Double): Rectangle = Rectangle(h, w)

  def circle(r: Double): Circle = Circle(r)

  def image(file: String): ImagePic = ImagePic(file)
}

case class Rectangle(h: Double, w: Double) extends VectorPicture

case class Circle(r: Double) extends VectorPicture

case class ImagePic(file: String) extends RasterPicture

object PicStack {
  def apply[T <: AnyDrawing](ps: Picture[T]*): PicStack[T] = PicStack(ps.toVector)

  def apply[T <: AnyDrawing](ps: List[Picture[T]]): PicStack[T] = PicStack(ps.toVector)
}

case class PicStack[T <: AnyDrawing](ps: Vector[Picture[T]]) extends Picture[T]

trait Transform[T <: AnyDrawing] extends Picture[T] {
  def pic: Picture[T]

  //  def *(other: Transform) = {
  //
  //  }
}

trait VectorTransform extends VectorPicture with Transform[VectorDrawing] {
  //  def pic: VectorPicture
}

trait RasterTransform extends RasterPicture with Transform[RasterDrawing] {
  //  def pic: RasterPicture
}

case class Translate[T <: AnyDrawing](x: Double, y: Double, pic: Picture[T]) extends Transform[T]

case class Rotate[T <: AnyDrawing](a: Double, pic: Picture[T]) extends Transform[T]

case class Scale[T <: AnyDrawing](fx: Double, fy: Double, pic: Picture[T]) extends Transform[T]

case class Stroke(c: Color, pic: VectorPicture) extends VectorTransform

case class Fill(c: Color, pic: VectorPicture) extends VectorTransform

case class Brighten(f: Double, pic: VectorPicture) extends VectorTransform

