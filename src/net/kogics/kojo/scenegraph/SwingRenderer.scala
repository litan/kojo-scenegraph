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

import java.awt.geom.{AffineTransform, Ellipse2D, Rectangle2D}
import java.awt.{BasicStroke, Color, Dimension, Graphics, Graphics2D, GraphicsEnvironment, Image, Paint, RenderingHints, Transparency}
import javax.swing.{JFrame, JPanel}

import scala.collection.mutable

class SwingRenderer(frame: JFrame) extends Renderer {
  type PlatformGraphics = Graphics
  var root: AnyPicture = _
  val cwidth = 1024
  val cheight = 768
  val bg = new Color(255, 170, 29)
  //    setBorder(BorderFactory.createLineBorder(Color.black))
  val dims = new Dimension(cwidth, cheight)
  val stroke = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)
  val backBuffer: Image = {
    val graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment.
      getDefaultScreenDevice.getDefaultConfiguration
    graphicsConfiguration.createCompatibleImage(cwidth, cheight, Transparency.TRANSLUCENT)
    //      new BufferedImage(cwidth, cheight, BufferedImage.TYPE_INT_ARGB);
  }

  val canvas = new JPanel {
    override def getPreferredSize = dims

    override def paintComponent(g: Graphics): Unit = {
      drawComponent(g, true)
    }
  }
  frame.getContentPane.add(canvas)

  def render(p: AnyPicture): Unit = {
    root = p
    //    canvas.invalidate()
  }

  def renderDirect(p: AnyPicture, g: Graphics): Unit = {
    root = p
    drawComponent(g, false)
  }

  def drawComponent(g: Graphics, useBb: Boolean): Unit = {
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

  var rdCount = 0

  def draw(pic: AnyPicture, g: Graphics2D): Unit = pic match {
    case Rectangle(h, w) =>
      val shape = new Rectangle2D.Double(0, 0, w, h)
      g.draw(shape)
    case Circle(r) =>
      val shape = new Ellipse2D.Double(0, 0, r * 2, r * 2)
      g.draw(shape)
    case PicStack(ps) => ps foreach (draw(_, g))
    case Translate(x, y, p) => saveTransform(g); g.translate(x, y); draw(p, g); restoreTransform(g)
    case Rotate(a, p) => saveTransform(g); g.rotate(a.toRadians); draw(p, g); restoreTransform(g)
    case ScaleXY(fx, fy, p) => saveTransform(g); g.scale(fx, fy); draw(p, g); restoreTransform(g)
    case Stroke(c, p) => savePaint(g); g.setPaint(c); draw(p, g); restorePaint(g)
    case Fill(c, p) => savePaint(g); g.setPaint(brighten(c)); fill(p, g); restorePaint(g); draw(p, g)
    case Brighten(f, p) => pushBrighten(f); draw(p, g); popBrighten()
    case null =>
  }

  def fill(pic: AnyPicture, g: Graphics2D): Unit = pic match {
    case Rectangle(h, w) =>
      val shape = new Rectangle2D.Double(0, 0, w, h)
      g.fill(shape)
    case Circle(r) =>
      val shape = new Ellipse2D.Double(0, 0, r * 2, r * 2)
      g.fill(shape)
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
