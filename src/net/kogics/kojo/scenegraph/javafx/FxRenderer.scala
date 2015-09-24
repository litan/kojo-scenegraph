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

package net.kogics.kojo.scenegraph.javafx

import _root_.javafx.scene.canvas.GraphicsContext
import _root_.javafx.scene.paint.{Color, Paint}
import _root_.javafx.scene.shape.{StrokeLineCap, StrokeLineJoin}
import _root_.javafx.scene.transform.Affine

import net.kogics.kojo.scenegraph._

import scala.collection.mutable

class FxRenderer(width: Int, height: Int) extends Renderer {
  type PlatformGraphics = GraphicsContext
  //  val bg = Color.WHITE
  //  val bg = Color.rgb(255, 170, 29)
  val bg = Color.rgb(0, 0, 250, 20.0 / 255)

  def render(p: AnyPicture, g2: GraphicsContext): Unit = {
    g2.clearRect(0, 0, width, height)
    g2.setFill(bg)
    g2.fillRect(0, 0, width, height)
    g2.setStroke(Color.BLACK)
    g2.setLineCap(StrokeLineCap.ROUND)
    g2.setLineJoin(StrokeLineJoin.ROUND)
    g2.setLineWidth(2)
    g2.translate(width / 2, height / 2)
    g2.scale(1, -1)
    val t1 = System.nanoTime()
    draw(p, g2)
    val t2 = System.nanoTime()
    //    println(s"Scene rendering time: ${(t2 - t1) / 1e9}")
  }

  var rdCount = 0

  def draw(pic: AnyPicture, g: GraphicsContext): Unit = pic match {
    case Rectangle(h, w) =>
      g.strokeRect(0, 0, w, h)
    case Circle(r) =>
      g.strokeOval(0, 0, r * 2, r * 2)
    case PicStack(ps) => ps foreach (draw(_, g))
    case Translate(x, y, p) => saveTransform(g); g.translate(x, y); draw(p, g); restoreTransform(g)
    case Rotate(a, p) => saveTransform(g); g.rotate(a); draw(p, g); restoreTransform(g)
    case ScaleXY(fx, fy, p) => saveTransform(g); g.scale(fx, fy); draw(p, g); restoreTransform(g)
    case Stroke(c, p) => saveStroke(g); g.setStroke(awtColor2fx(c)); draw(p, g); restoreStroke(g)
    case Fill(c, p) => saveFill(g); g.setFill(brighten(c)); fill(p, g); restoreFill(g); draw(p, g)
    case Brighten(f, p) => pushBrighten(f); draw(p, g); popBrighten()
    case null =>
  }

  def fill(pic: AnyPicture, g: GraphicsContext): Unit = pic match {
    case Rectangle(h, w) =>
      g.fillRect(0, 0, w, h)
    case Circle(r) =>
      g.fillOval(0, 0, r * 2, r * 2)
    case _ =>
  }

  val savedStrokes = new mutable.Stack[Paint]
  val savedFills = new mutable.Stack[Paint]
  val savedTransforms = new mutable.Stack[Affine]
  val brightenStack = new mutable.Stack[Double]

  def fillColor(fillPaint: Paint) = fillPaint match {
    case null => Color.WHITE
    case c: Color => c
    case _ => throw new IllegalStateException("You can't extract rgb values of non Color paints")
  }

  def awtColor2fx(c: java.awt.Color) = {
    Color.rgb(c.getRed, c.getGreen, c.getBlue, c.getAlpha / 255)
  }

  def saveStroke(g: GraphicsContext): Unit = {
    savedStrokes.push(g.getStroke)
  }

  def restoreStroke(g: GraphicsContext): Unit = {
    g.setStroke(savedStrokes.pop())
  }

  def saveFill(g: GraphicsContext): Unit = {
    savedFills.push(g.getFill)
  }

  def restoreFill(g: GraphicsContext): Unit = {
    g.setFill(savedFills.pop())
  }

  def saveTransform(g: GraphicsContext): Unit = {
    savedTransforms.push(g.getTransform)
  }

  def restoreTransform(g: GraphicsContext): Unit = {
    g.setTransform(savedTransforms.pop())
  }

  def pushBrighten(f: Double): Unit = {
    brightenStack.push(f)
  }

  def popBrighten(): Unit = {
    brightenStack.pop()
  }

  def brighten(c: java.awt.Color): Color = {
    val awtColor = brightenStack.foldLeft(c)((c, f) => Utils.britMod(c, f))
    awtColor2fx(awtColor)
  }
}
