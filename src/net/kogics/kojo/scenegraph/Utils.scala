package net.kogics.kojo.scenegraph

import java.awt.{Color, EventQueue}
import java.util
import java.util.concurrent.locks.ReentrantLock

/**
 * Created by lalit on 9/21/15.
 */
object Utils {
  def runLaterInSwingThread(fn: => Unit) {
    javax.swing.SwingUtilities.invokeLater(new Runnable {
      override def run {
        fn
      }
    })
  }

  def inSwingThread = EventQueue.isDispatchThread

  val batchLock = new ReentrantLock
  val notFull = batchLock.newCondition
  val Max_Q_Size = 9000
  val batchQ = new util.LinkedList[() => Unit]

  // this is the core of Kojo UI performance - so the code is a little low-level
  def runInSwingThread(fn: => Unit) {
    if (EventQueue.isDispatchThread) {
      fn
    }
    else {
      batchLock.lock()
      try {
        while (batchQ.size > Max_Q_Size) {
          notFull.await()
        }
        val needDrainer = batchQ.isEmpty
        batchQ.add(fn _)
        if (needDrainer) {
          javax.swing.SwingUtilities.invokeLater(new Runnable {
            override def run {
              batchLock.lock()
              while (!batchQ.isEmpty) {
                try {
                  batchQ.remove.apply()
                }
                catch {
                  case t: Throwable =>
                    Utils.runLaterInSwingThread {
                      reportException(t)
                    }
                }
              }
              notFull.signal()
              batchLock.unlock()
            }
          })
        }
      }
      finally {
        batchLock.unlock()
      }
    }
  }

  def runInSwingThreadAndWait[T](fn: => T): T = {
    if (inSwingThread) {
      fn
    }
    else {
      var t: T = null.asInstanceOf[T]
      javax.swing.SwingUtilities.invokeAndWait(new Runnable {
        override def run {
          t = fn
        }
      })
      t
    }
  }

  def reportException(t: Throwable) {
    println(s"Problem - ${t.toString} (see log for details)")
  }

  private def rgbaComps(color: Color) = (color.getRed, color.getGreen, color.getBlue, color.getAlpha())

  def checkHsbModFactor(f: Double) {
    if (f < -1 || f > 1) {
      throw new IllegalArgumentException("mod factor needs to be between -1 and 1")
    }
  }

  private def modHsb(q: Double, f: Double) = {
    checkHsbModFactor(f)

    if (f > 0) {
      q * (1 - f) + f
    }
    else {
      q * (1 + f)
    }
  }

  def hsbColor(h: Float, s: Float, b: Float, a: Int) = {
    val newrgb = Color.HSBtoRGB(h, s, b)
    new Color((newrgb & 0x00ffffff) | (a << 24), true)
  }

  def hueMod(c: Color, f: Double) = {
    val (r, g, b, a) = rgbaComps(c)
    val hsb = Color.RGBtoHSB(r, g, b, null)
    val h = modHsb(hsb(0), f).toFloat
    hsbColor(h, hsb(1), hsb(2), a)
  }

  def satMod(c: Color, f: Double) = {
    val (r, g, b, a) = rgbaComps(c)
    val hsb = Color.RGBtoHSB(r, g, b, null)
    val s = modHsb(hsb(1), f).toFloat
    hsbColor(hsb(0), s, hsb(2), a)
  }

  def britMod(c: Color, f: Double) = {
    val (r, g, b, a) = rgbaComps(c)
    val hsb = Color.RGBtoHSB(r, g, b, null)
    val br = modHsb(hsb(2), f).toFloat
    hsbColor(hsb(0), hsb(1), br, a)
  }
}
