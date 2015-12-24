package net.kogics.kojo.scenegraph.javafx

import _root_.javafx.animation.AnimationTimer
import _root_.javafx.application.Application
import _root_.javafx.event.EventHandler
import _root_.javafx.scene.canvas.Canvas
import _root_.javafx.scene.input.MouseEvent
import _root_.javafx.scene.paint.Color
import _root_.javafx.scene.{Group, Scene}
import _root_.javafx.stage.{Screen, Stage}

import net.kogics.kojo.scenegraph._

object FxAnimationMain {
  def main(args: Array[String]) {
    Application.launch(classOf[FxAnimationMain], args: _*);
  }
}

class FxAnimationMain extends Application {
  override def start(primaryStage: Stage) {
    primaryStage.setTitle("Rectangle Anime")
    //    primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH)
    //    primaryStage.setFullScreen(true)
    init(primaryStage)
    primaryStage.show()
  }

  val screenSize = Screen.getPrimary.getVisualBounds

  def translate(p: AnyPicture, deltaFactor: Double): AnyPicture = Translate(-3 * deltaFactor, 0, p)

  def init(stage: Stage) {
    stage.setScene(scene(screenSize.getWidth, screenSize.getHeight))

    def tick(delta: Double): Unit = {
      val df = delta / 16
      //      println(df)
      pic = translate(pic, df)
      gc.save()
      renderer.render(pic, gc)
      gc.restore()
    }

    //    val timeline = new Timeline(
    //      new KeyFrame(
    //        Duration.millis(30),
    //        new EventHandler[ActionEvent] {
    //          override def handle(event: ActionEvent): Unit = {
    //            tick()
    //          }
    //        }
    //      ))
    //    timeline.setCycleCount(Animation.INDEFINITE)
    //    timeline.play();


    val timer = new AnimationTimer {
      var prev = System.nanoTime()

      def handle(t: Long): Unit = {
        //        println((t - prev) / 1e6)
        tick((t - prev) / 1e6)
        prev = t
      }
    }
    timer.start()
  }

  def scene(width: Double, height: Double) = {
    val root = new Group
    val scene = new Scene(root, width, height, Color.WHITE)
    populateScene(scene, width, height)
    scene
  }

  def clearScene(scene: Scene) {
    val root = new Group
    scene.setRoot(root)
  }

  var pic: AnyPicture = trans(400, 0) * penColor(noColor) * fillColor(java.awt.Color.blue) -> PicShape.rect(200, 40)
  val canvas = new Canvas(screenSize.getWidth, screenSize.getHeight)
  val gc = canvas.getGraphicsContext2D
  val renderer = new FxRenderer(screenSize.getWidth.toInt, screenSize.getHeight.toInt)
  canvas.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      println("\nBam!\n")
      System.exit(0)
    }
  })

  def populateScene(scene: Scene, width: Double, height: Double) {
    val root = new Group
    scene.setRoot(root)
    gc.save()
    renderer.render(pic, gc)
    gc.restore()
    root.getChildren.add(canvas)
  }
}
