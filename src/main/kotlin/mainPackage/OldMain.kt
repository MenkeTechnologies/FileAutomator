package mainPackage

import javafx.animation.FadeTransition
import javafx.application.Application
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.fxml.FXMLLoader
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.effect.DropShadow
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import windows.Settings
import windows.Splash
import java.io.IOException
import java.util.prefs.Preferences

class OldMain : Application() {
    var mainScene: Scene? = null
    var maxSize = 100
    var loader: FXMLLoader? = null
    var root: Parent? = null
    var mainController: MainController? = null
    private var splashLayout: Pane? = null
    private var loadProgress: ProgressBar? = null
    private var progressText: Label? = null
    private var primaryStage: Stage? = null

    override fun init() {
        val splash = ImageView(Image(SPLASH_IMAGE))
        loadProgress = ProgressBar()
        loadProgress!!.prefWidth = (SPLASH_WIDTH - 20).toDouble()
        progressText = Label("Will find friends for peanuts . . .")
        splashLayout = VBox()
        (splashLayout as VBox).children.addAll(splash, loadProgress, progressText)
        progressText!!.alignment = Pos.CENTER

        val color = CommonUtilities.toWebColor(Color.valueOf(Preferences.userRoot().get("backgroundColorPicker", null)))

        var style = "-fx-padding: 5; " +
                "-fx-border-width:5; " +
                "-fx-border-color: " +
                "linear-gradient(" +
                "to bottom, " +
                "chocolate, " +
                "derive(chocolate, 50%)" +
                ");"

        if (color != null) {
            style += "-fx-base: $color"
        }

        System.err.println("___________${Thread.currentThread().stackTrace[1].className}____Line:${Thread.currentThread().stackTrace[1].lineNumber}___ style$style")

        splashLayout!!.style = style
        splashLayout!!.effect = DropShadow()
    }

    private fun showSplash(initStage: Stage, initCompletionHandler: Splash.InitCompletionHandler) {
        val splashScene = Scene(splashLayout, Color.TRANSPARENT)
        val bounds: Rectangle2D = Screen.getPrimary().bounds
        initStage.toFront()

        val loadingTask = object : CustomTask<String>() {
            override fun call(): String {
                updateMessage("Starting...")
                updateProgress(0.0, 100.0)
                Thread.sleep(2000)
                initMainStage(this)
                return "Done"
            }
        }
        loadProgress!!.progressProperty().bind(loadingTask.progressProperty())
        progressText!!.textProperty().bind(loadingTask.messageProperty())

        initStage.scene = splashScene
        initStage.x = bounds.minX + bounds.width / 2 - SPLASH_WIDTH / 2
        initStage.y = bounds.minY + bounds.height / 2 - SPLASH_HEIGHT / 2
        initStage.initStyle(StageStyle.TRANSPARENT)
        initStage.isAlwaysOnTop = true
        initStage.show()

        System.err.println("___________${Thread.currentThread().stackTrace[1].className}____Line:${Thread.currentThread().stackTrace[1].lineNumber}___ here")

        loadingTask.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress!!.progressProperty().unbind()
                loadProgress!!.progress = 1.0

                val fadeSplash = FadeTransition(Duration.seconds(1.2), splashLayout)
                fadeSplash.fromValue = 1.0
                fadeSplash.toValue = 0.0
                fadeSplash.play()
                fadeSplash.setOnFinished {
                    initStage.hide()
                    initCompletionHandler.complete()
                }
            }
        }

        Thread(loadingTask).start()
    }

    override fun start(initStage: Stage) {
        showSplash(initStage, object : Splash.InitCompletionHandler {
            override fun complete() { showMainStage() }
        })
    }

    @Throws(Exception::class)
    private fun initMainStage(loadingTask: CustomTask<String>) {
        loadingTask.updateProgress(0.0, maxSize.toDouble())
        val sleepTime = 400L
        Thread.sleep(sleepTime)

        Platform.runLater {
            primaryStage = Stage()
            loader = FXMLLoader(javaClass.getResource("/main.fxml"))
        }

        loadingTask.updateProgress(20.0, maxSize.toDouble())
        loadingTask.updateMessage("Loading UI...")

        Thread.sleep(sleepTime)

        Platform.runLater {
            try {
                root = loader!!.load()
                mainController = loader!!.getController()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        loadingTask.updateProgress(40.0, maxSize.toDouble())
        loadingTask.updateMessage("Getting Controller...")

        Thread.sleep(sleepTime)
        Platform.runLater {
            primaryStage!!.title = "File Automator"
            primaryStage!!.setOnCloseRequest {
                if (mainController!!.mainSplitPane.items.size > 1) {
                    for (i in 0 until mainController!!.mainSplitPane.dividers.size) {
                        Preferences.userRoot().putDouble("dividerPos$i", mainController!!.mainSplitPane.dividerPositions[i])
                    }
                }
                Platform.exit()
                System.exit(0)
            }
        }
        loadingTask.updateProgress(60.0, maxSize.toDouble())
        loadingTask.updateMessage("Creating Scene...")
        Thread.sleep(sleepTime)
        Platform.runLater {
            mainScene = Scene(root, 1800.0, 1200.0)
            mainController!!.initBindings()
        }

        loadingTask.updateProgress(80.0, maxSize.toDouble())
        loadingTask.updateMessage("Creating Bindings...")
        Thread.sleep(sleepTime)

        Platform.runLater {
            Settings.initMenuBar(mainController!!, mainScene!!, primaryStage!!)

            val sps = doubleArrayOf(0.0, 0.0, 0.0)

            for (i in 0 until mainController!!.mainSplitPane.dividers.size) {
                val sp = Preferences.userRoot().getDouble("dividerPos$i", mainController!!.mainSplitPane.dividerPositions[i])
                sps[i] = sp
            }

            mainController!!.mainSplitPane.setDividerPositions(*sps)

            mainScene!!.stylesheets.add("stylesheets/styles.css")
        }

        loadingTask.updateProgress(87.0, maxSize.toDouble())
        loadingTask.updateMessage("Adding styles...")
        Thread.sleep(sleepTime)
        loadingTask.updateProgress(93.0, maxSize.toDouble())
        loadingTask.updateMessage("Adding keybindings...")

        Platform.runLater {
            initKeyBindings(mainController!!)
        }

        loadingTask.updateProgress(100.0, maxSize.toDouble())
        loadingTask.updateMessage("Done...")
    }

    private fun showMainStage() {
        primaryStage!!.scene = mainScene
        primaryStage!!.show()
    }

    interface InitCompletionHandler {
        fun complete()
    }

    companion object {
        const val APPLICATION_ICON = "http://cdn1.iconfinder.com/data/icons/Copenhagen/PNG/32/people.png"
        const val SPLASH_IMAGE = "http://fxexperience.com/wp-content/uploads/2010/06/logo.png"
        private const val SPLASH_WIDTH = 676
        private const val SPLASH_HEIGHT = 227

        @JvmStatic
        fun initKeyBindings(mainController: MainController) {
            val eventHandler = javafx.event.EventHandler<KeyEvent> { e ->
                mainController.initMainTableViewKeyBindings()
                mainController.initTreeViewKeyBindings()

                if (e.code == KeyCode.SPACE) {
                    if (MainController.mediaPlayer != null) {
                        if (MainController.mediaPlayer.status == MediaPlayer.Status.PLAYING) {
                            MainController.mediaPlayer.pause()
                        } else {
                            MainController.mediaPlayer.play()
                        }
                    }
                    e.consume()
                }

                if (e.code == KeyCode.F) {
                    if (Utilities.maximized.get()) {
                        mainController.restorePanesToOld("mediaContextMenu")
                    } else {
                        mainController.maximizeVideo(null)
                        Utilities.maximized.set(true)
                    }
                    e.consume()
                }

                if (e.code == KeyCode.Q) {
                    mainController.fitScreenAction(null, 0.25)
                    e.consume()
                }
                if (e.code == KeyCode.W) {
                    mainController.fitScreenToggleMediaButton.isSelected = !mainController.fitScreenToggleMediaButton.isSelected
                    mainController.fitScreenAction(null, 1.0)
                    e.consume()
                }

                if (MainController.mediaPlayer != null) {
                    if (e.code == KeyCode.N) {
                        mainController.playNext(null)
                        mainController.mediaPlayerControls.isVisible = true
                        mainController.hideNodeAfterDelay(mainController.mediaPlayerControls)
                        e.consume()
                    }
                    if (e.code == KeyCode.P) {
                        Utilities.endOfMediaAction(mainController, false)
                        mainController.mediaPlayerControls.isVisible = true
                        mainController.hideNodeAfterDelay(mainController.mediaPlayerControls)
                        e.consume()
                    }

                    for (digit in 0..9) {
                        val keyCode = KeyCode.valueOf("DIGIT$digit")
                        if (e.code == keyCode) {
                            MainController.mediaPlayer.seek(Duration.minutes(digit.toDouble()))
                            e.consume()
                        }
                    }

                    if (e.code == KeyCode.B) {
                        MainController.mediaPlayer.seek(MainController.mediaPlayer.currentTime.subtract(Duration.seconds(15.0)))
                        e.consume()
                    }
                }
            }

            mainController.mainSplitPane.items[0].addEventFilter(KeyEvent.KEY_PRESSED, eventHandler)
            mainController.mainSplitPane.items[1].addEventFilter(KeyEvent.KEY_PRESSED, eventHandler)
            mainController.mainSplitPane.items[2].addEventFilter(KeyEvent.KEY_PRESSED, eventHandler)
        }

        @JvmStatic
        fun main(args: Array<String>) {
            launch(OldMain::class.java, *args)
        }
    }
}
