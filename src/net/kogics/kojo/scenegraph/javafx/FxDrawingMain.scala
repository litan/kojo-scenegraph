package net.kogics.kojo.scenegraph.javafx

import javafx.application.Application
import javafx.scene.canvas.Canvas
import javafx.scene.input.KeyCombination
import javafx.scene.paint.Color
import javafx.scene.{Group, Scene}
import javafx.stage.{Screen, Stage}

import net.kogics.kojo.scenegraph.TestDrawingDsl

object FxDrawingMain {
  def main(args: Array[String]) {
    Application.launch(classOf[FxDrawingMain], args: _*);
  }
}

class FxDrawingMain extends Application {
  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Rectangle Anime")
    primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH)
    //    primaryStage.setFullScreen(true)
    init(primaryStage)
    primaryStage.show()
  }

  val screenSize = Screen.getPrimary.getVisualBounds

  def init(stage: Stage) {
    stage.setScene(scene(screenSize.getWidth, screenSize.getHeight))
  }

  def scene(width: Double, height: Double) = {
    val root = new Group
    val scene = new Scene(root, width, height, Color.WHITE)
    populateScene(scene, width, height)
    scene
  }

  def populateScene(scene: Scene, width: Double, height: Double) {
    val root = new Group
    scene.setRoot(root)
    val pic = TestDrawingDsl.drawing1(17)
    val renderer = new FxRenderer(width.toInt, height.toInt)
    val canvas = new Canvas(width, height)
    val gc = canvas.getGraphicsContext2D
    for (i <- 1 to 3) {
      gc.save()
      renderer.render(pic, gc)
      gc.restore()
    }
    root.getChildren.add(canvas)
  }
}
