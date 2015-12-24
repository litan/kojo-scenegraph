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

package net.kogics.kojo.scenegraph.swing

import java.awt.{Color, Frame, GraphicsEnvironment, Toolkit}

import net.kogics.kojo.scenegraph._

object SwingAnimationMain {
  def main(args: Array[String]) {
    println("Welcome to the Kojo Scenegraph Animation App.")
    println(s"Java version: ${System.getProperty("java.version")}")

    //    System.setProperty("sun.java2d.opengl", "True")
    //    System.setProperty("sun.java2d.xrender", "True")

    val numBuffers = 2

    val env = GraphicsEnvironment.getLocalGraphicsEnvironment
    val device = env.getDefaultScreenDevice
    val refreshRate = device.getDisplayMode.getRefreshRate * 2
    println("Refresh rate: " + refreshRate)
    val gc = device.getDefaultConfiguration
    val bufCap = gc.getBufferCapabilities
    println("Full Screen Required: " + bufCap.isFullScreenRequired)
    println("Multi Buf: " + bufCap.isMultiBufferAvailable)
    println("Page Flipping: " + bufCap.isPageFlipping)
    val frame = new Frame(gc)
    frame.setUndecorated(true)
    frame.setIgnoreRepaint(true)
    device.setFullScreenWindow(frame)
    val bounds = frame.getBounds();
    val renderer = new SwingRenderer(bounds.getWidth.toInt, bounds.getHeight.toInt, false)
    frame.createBufferStrategy(numBuffers)
    val bufferStrategy = frame.getBufferStrategy()
    println(bufferStrategy)
    var sceneRoot: AnyPicture = trans(400, 0) * penColor(noColor) * fillColor(Color.blue) -> PicShape.rect(200, 40)
    def translate(p: AnyPicture): AnyPicture = Translate(-1, 0, p)
    var cnt = 0
    while (cnt < 200) {
      do {
        do {
          val g = bufferStrategy.getDrawGraphics
          renderer.render(sceneRoot, g)
          g.dispose()
        } while (bufferStrategy.contentsRestored)
        bufferStrategy.show()
      } while (bufferStrategy.contentsLost)

      sceneRoot = translate(sceneRoot)
      Toolkit.getDefaultToolkit().sync()
      Thread.sleep((1000.0 / refreshRate).toInt)
      cnt += 1
    }
    device.setFullScreenWindow(null)
    System.exit(0)
  }
}
