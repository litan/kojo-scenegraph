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

import java.awt.{Dimension, Graphics}
import javax.swing.{JFrame, JPanel}

import net.kogics.kojo.scenegraph.{TestDrawingDsl, Utils}

object SwingDrawingMain {
  def main(args: Array[String]) {
    println("Welcome to the Kojo Scenegraph Drawing App.")
    println(s"Java version: ${System.getProperty("java.version")}")

    Utils.runInSwingThread {
      val width = 1024
      val height = 768
      val frame = new JFrame("Kojo Scenegraph")
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      frame.setSize(1024, 768)

      val t1 = System.nanoTime()
      val sceneRoot = TestDrawingDsl.drawing1(21)
      val t2 = System.nanoTime()
      println(s"Scene creation time: ${(t2 - t1) / 1e9}")

      val dims = new Dimension(width, height)
      val renderer = new SwingRenderer(width, height, true)
      val canvas = new JPanel {
        override def getPreferredSize = dims

        override def paintComponent(g: Graphics): Unit = {
          renderer.render(sceneRoot, g)
        }
      }

      frame.getContentPane.add(canvas)
      frame.setVisible(true)
    }
  }
}

