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

import javax.swing.JFrame

object Main {
  def main(args: Array[String]) {
    println("Welcome to the Kojo Scenegraph experiment.")
    println(s"Java version: ${System.getProperty("java.version")}")

    Utils.runInSwingThread {
      val frame = new JFrame("Kojo Scenegraph")
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      frame.setSize(1024, 768)


      val t1 = System.nanoTime()
      val sceneRoot = TestDrawingDsl.drawing1(15)
      val t2 = System.nanoTime()
      println(s"Scene creation time: ${(t2 - t1) / 1e9}")


      val renderer = new SwingRenderer
      frame.getContentPane.add(renderer.canvas)
      renderer.render(sceneRoot)
      frame.setVisible(true)
    }
  }
}

