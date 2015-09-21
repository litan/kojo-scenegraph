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

import java.awt._
import java.awt.geom.AffineTransform
import javax.swing.{JFrame, JPanel}

import scala.collection.mutable

object Main {
  def main(args: Array[String]) {
    println("Welcome to the Kojo Scenegraph experiment.")
    println(s"Java version: ${System.getProperty("java.version")}")

    Utils.runInSwingThread {
      val frame = new JFrame("Kojo Scenegraph")
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      frame.setSize(1024, 768)

      val noColor = new Color(0, 0, 0, 0)
      val size = 100
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



      val t1 = System.nanoTime()
      val sceneRoot = drawing(15)
      //      val rect = PicShape.rect(100, 100)
      //      val sceneRoot = Stroke(Color.yellow, Stack(Vector(
      //        Translate(-200, 0, Brighten(-0.5, Fill(new Color(0, 94, 0), rect))),
      //        Translate(0, 0, Brighten(0, Fill(new Color(0, 94, 0), rect))),
      //        Translate(200, 0, Brighten(0.5, Fill(new Color(0, 94, 0), rect)))
      //      )))
      val t2 = System.nanoTime()
      println(s"Scene creation time: ${(t2 - t1) / 1e9}")


      val renderer = new SwingRenderer
      frame.getContentPane.add(renderer.canvas)
      frame.setVisible(true)
      renderer.render(sceneRoot)
    }
  }
}


object PicShape {
  def rect(h: Double, w: Double): Rectangle = Rectangle(h, w)
}

trait Picture {

}

case class Rectangle(h: Double, w: Double) extends Picture

case class Stack(ps: Seq[Picture]) extends Picture

trait Transform extends Picture

case class Translate(x: Double, y: Double, p: Picture) extends Transform

case class Rotate(a: Double, p: Picture) extends Transform

case class Scale(fx: Double, fy: Double, p: Picture) extends Transform

case class Stroke(c: Color, p: Picture) extends Transform

case class Fill(c: Color, p: Picture) extends Transform

case class Brighten(f: Double, p: Picture) extends Transform

class SwingRenderer {
  var root: Picture = _
  val stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)

  val canvas = new JPanel {
    val cwidth = 1024
    val cheight = 768
    val bg = new Color(255, 170, 29)
    //    setBorder(BorderFactory.createLineBorder(Color.black))
    val dims = new Dimension(cwidth, cheight)

    override def getPreferredSize = dims

    val backBuffer: Image = {
      val graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment.
        getDefaultScreenDevice.getDefaultConfiguration
      graphicsConfiguration.createCompatibleImage(cwidth, cheight, Transparency.OPAQUE)
      //      new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
    }

    val useBb = true

    override def paintComponent(g: Graphics): Unit = {
      val g2 = if (useBb) {
        backBuffer.getGraphics.asInstanceOf[Graphics2D]
      }
      else {
        g.asInstanceOf[Graphics2D]
      }
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
      g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
      g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON)
      g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE)
      g2.setPaint(bg)
      g2.fillRect(0, 0, cwidth, cheight)
      g2.setPaint(Color.black)
      g2.translate(1024 / 2, 768 / 2)
      g2.scale(1, -1)
      g2.setStroke(stroke)
      val t1 = System.nanoTime()
      draw(root, g2)

      if (useBb) {
        val g2o = g.asInstanceOf[Graphics2D]
        g2o.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)
        g2o.drawImage(backBuffer, 0, 0, cwidth, cheight, null)
      }

      val t2 = System.nanoTime()
      println(s"Scene rendering time: ${(t2 - t1) / 1e9}")
    }
  }

  def render(p: Picture): Unit = {
    root = p
    canvas.invalidate()
  }

  var rdCount = 0

  def draw(pic: Picture, g: Graphics2D): Unit = pic match {
    case Rectangle(h, w) =>
      //      rdCount += 1
      //      println(s"Drawing Rect ($rdCount)")
      g.drawRect(0, 0, w.toInt, h.toInt)
    case Stack(ps) => ps foreach (draw(_, g))
    case Translate(x, y, p) => saveTransform(g); g.translate(x, y); draw(p, g); restoreTransform(g)
    case Rotate(a, p) => saveTransform(g); g.rotate(a.toRadians); draw(p, g); restoreTransform(g)
    case Scale(fx, fy, p) => saveTransform(g); g.scale(fx, fy); draw(p, g); restoreTransform(g)
    case Stroke(c, p) => savePaint(g); g.setPaint(c); draw(p, g); restorePaint(g)
    case Fill(c, p) => savePaint(g); g.setPaint(brighten(c)); fill(p, g); restorePaint(g); draw(p, g)
    case Brighten(f, p) => pushBrighten(f); draw(p, g); popBrighten()
    case null =>
  }

  def fill(pic: Picture, g: Graphics2D): Unit = pic match {
    case Rectangle(h, w) =>
      g.fillRect(0, 0, w.toInt, h.toInt)
    case _ =>
  }

  val savedPaints = new mutable.Stack[Paint]
  val savedTransforms = new mutable.Stack[AffineTransform]
  val brightenStack = new mutable.Stack[Double]

  def fillColor(fillPaint: Paint) = fillPaint match {
    case null => Color.white
    case c: Color => c
    case _ => throw new IllegalStateException("You can't extract rgb values of non Color paints")
  }

  def savePaint(g: Graphics2D): Unit = {
    savedPaints.push(g.getPaint)
  }

  def restorePaint(g: Graphics2D): Unit = {
    g.setPaint(savedPaints.pop())
  }

  def saveTransform(g: Graphics2D): Unit = {
    savedTransforms.push(g.getTransform)
  }

  def restoreTransform(g: Graphics2D): Unit = {
    g.setTransform(savedTransforms.pop())
  }

  def pushBrighten(f: Double): Unit = {
    brightenStack.push(f)
  }

  def popBrighten(): Unit = {
    brightenStack.pop()
  }

  def brighten(c: Color): Color = {
    brightenStack.foldLeft(c)((c, f) => Utils.britMod(c, f))
  }
}

