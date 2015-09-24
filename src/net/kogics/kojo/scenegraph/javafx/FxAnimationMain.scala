package net.kogics.kojo.scenegraph.javafx

import _root_.javafx.animation.AnimationTimer
import _root_.javafx.application.Application
import _root_.javafx.scene.canvas.Canvas
import _root_.javafx.scene.input.KeyCombination
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
    primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH)
    primaryStage.setFullScreen(true)
    init(primaryStage)
    primaryStage.show()
  }

  val screenSize = Screen.getPrimary.getVisualBounds

  def translate(p: AnyPicture): AnyPicture = Translate(-5, 0, p)

  def init(stage: Stage) {
    stage.setScene(scene(screenSize.getWidth, screenSize.getHeight))

    def tick(): Unit = {
      pic = translate(pic)
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
      def handle(t: Long): Unit = {
        tick()
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

  var pic: AnyPicture = trans(400, 0) * penColor(java.awt.Color.lightGray) * fillColor(new java.awt.Color(0, 230, 0)) -> PicShape.circle(15)
  val canvas = new Canvas(screenSize.getWidth, screenSize.getHeight)
  val gc = canvas.getGraphicsContext2D
  val renderer = new FxRenderer(screenSize.getWidth.toInt, screenSize.getHeight.toInt)

  def populateScene(scene: Scene, width: Double, height: Double) {
    val root = new Group
    scene.setRoot(root)
    gc.save()
    renderer.render(pic, gc)
    gc.restore()
    root.getChildren.add(canvas)
  }
}
