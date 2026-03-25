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
import javafx.scene.effect.Reflection
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.Border
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.media.MediaPlayer
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.util.Duration
import mainPackage.StylesheetUtilities.Modify
import windows.Settings
import windows.Splash
import java.io.File
import java.io.IOException
import java.util.prefs.Preferences

class Main : Application() {
    var mainScene: Scene? = null
    var maxSize = 100
    var loader: FXMLLoader? = null
    var root: Parent? = null
    var mainController: MainController? = null
    private var mainVBox: VBox? = null
    private var loadProgress: ProgressBar? = null
    private var progressText: Label? = null
    private var primaryStage: Stage? = null

    override fun init() {
        val newFile = File(System.getProperty("user.home") + File.separator + "modify.css")
        if (!newFile.exists()) {
            newFile.createNewFile()
        }

        val splash = ImageView(SPLASH_IMAGE)
        mainVBox = VBox()
        mainVBox!!.spacing = 0.0
        splash.isPreserveRatio = true
        mainVBox!!.border = Border.EMPTY

        loadProgress = ProgressBar()
        loadProgress!!.prefWidth = SPLASH_WIDTH.toDouble()
        loadProgress!!.prefHeight = 6.0
        loadProgress!!.style = """
            -fx-accent: #ff2a6d;
            -fx-control-inner-background: #0a0a12;
            -fx-background-color: #0a0a12;
        """.trimIndent()

        progressText = Label("Loading ...")
        progressText!!.style = """
            -fx-text-fill: #05d9e8;
            -fx-font-family: "Menlo", "Courier New", monospace;
            -fx-font-size: 12px;
            -fx-padding: 8 0 8 12;
        """.trimIndent()

        val stackpane = StackPane()
        stackpane.prefWidth = mainVBox!!.width
        splash.fitWidthProperty().bind(stackpane.widthProperty())

        val label = Text("File Automator")
        label.font = Font.font("Menlo", 72.0)
        label.fill = Color.web("#05d9e8")
        label.stroke = Color.web("#ff2a6d")
        label.strokeWidth = 1.5

        val titleGlow = javafx.scene.effect.Bloom()
        titleGlow.threshold = 0.6
        val titleShadow = javafx.scene.effect.DropShadow()
        titleShadow.color = Color.web("#05d9e8")
        titleShadow.radius = 30.0
        titleShadow.spread = 0.4
        titleShadow.input = titleGlow
        label.effect = titleShadow

        val versionLabel = Text("v2.0")
        versionLabel.font = Font.font("Menlo", 18.0)
        versionLabel.fill = Color.web("#ff2a6d")
        val versionGlow = javafx.scene.effect.DropShadow()
        versionGlow.color = Color.web("#ff2a6d")
        versionGlow.radius = 10.0
        versionGlow.spread = 0.3
        versionLabel.effect = versionGlow

        val titleBox = VBox(4.0)
        titleBox.alignment = Pos.CENTER
        titleBox.children.addAll(label, versionLabel)

        stackpane.children.addAll(splash, titleBox)

        val bottomVbox = VBox(0.0)
        bottomVbox.style = "-fx-background-color: #0a0a12; -fx-padding: 0;"
        bottomVbox.children.addAll(loadProgress, progressText)

        mainVBox!!.children.addAll(stackpane, bottomVbox)

        mainVBox!!.style = """
            -fx-background-color: #0a0a12;
            -fx-border-color: #05d9e8;
            -fx-border-width: 2;
            -fx-border-radius: 4;
            -fx-background-radius: 4;
        """.trimIndent()

        val outerGlow = javafx.scene.effect.DropShadow()
        outerGlow.color = Color.web("#05d9e8")
        outerGlow.radius = 25.0
        outerGlow.spread = 0.2
        mainVBox!!.effect = outerGlow
    }

    private fun showSplash(initStage: Stage, initCompletionHandler: Splash.InitCompletionHandler) {
        val splashScene = Scene(mainVBox, SPLASH_WIDTH.toDouble(), SPLASH_HEIGHT.toDouble(), Color.TRANSPARENT)
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

        loadingTask.stateProperty().addListener { _, _, newState ->
            if (newState == Worker.State.SUCCEEDED) {
                loadProgress!!.progressProperty().unbind()
                loadProgress!!.progress = 1.0

                val fadeSplash = FadeTransition(Duration.seconds(0.6), mainVBox)
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
            override fun complete() {
                showMainStage()
            }
        })
    }

    @Throws(Exception::class)
    private fun initMainStage(loadingTask: CustomTask<String>) {
        loadingTask.updateProgress(0.0, maxSize.toDouble())
        val sleepTime = 50L
        Thread.sleep(sleepTime)

        loadingTask.updateProgress(20.0, maxSize.toDouble())
        loadingTask.updateMessage("Loading UI...")

        Thread.sleep(sleepTime)
        loadingTask.updateProgress(40.0, maxSize.toDouble())
        loadingTask.updateMessage("Getting Controller...")

        Thread.sleep(sleepTime)

        Platform.runLater { }
        loadingTask.updateProgress(60.0, maxSize.toDouble())
        loadingTask.updateMessage("Creating Scene...")
        Thread.sleep(sleepTime)

        loadingTask.updateProgress(80.0, maxSize.toDouble())
        loadingTask.updateMessage("Creating Bindings...")
        Platform.runLater { }

        loadingTask.updateProgress(87.0, maxSize.toDouble())
        loadingTask.updateMessage("Adding styles...")
        Thread.sleep(sleepTime)
        loadingTask.updateProgress(93.0, maxSize.toDouble())
        loadingTask.updateMessage("Adding keybindings...")

        loadingTask.updateProgress(100.0, maxSize.toDouble())
        loadingTask.updateMessage("Done...")
    }

    private fun showMainStage() {
        primaryStage = Stage()
        loader = FXMLLoader(javaClass.getResource("/main.fxml"))

        try {
            root = loader!!.load()
            mainController = loader!!.getController()
        } catch (e: IOException) {
            e.printStackTrace()
        }

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
        mainScene = Scene(root, 1800.0, 1200.0)

        mainController!!.initBindings()

        Settings.initMenuBar(mainController!!, mainScene!!, primaryStage!!)

        val sps = doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0)

        for (i in 0 until mainController!!.mainSplitPane.dividers.size) {
            val sp = Preferences.userRoot().getDouble("dividerPos$i", mainController!!.mainSplitPane.dividerPositions[i])
            sps[i] = sp
        }

        mainController!!.mainSplitPane.setDividerPositions(*sps)

        initKeyBindings(mainController!!)

        primaryStage!!.scene = mainScene

        Modify.scenes.add(mainScene)

        Modify.addStyleSheets(Modify.scenes, Modify.tempCssFile)

        primaryStage!!.show()
    }

    interface InitCompletionHandler {
        fun complete()
    }

    companion object {
        @JvmField
        val SPLASH_IMAGE: Image = Image(MainController::class.java.getResourceAsStream("/splash.jpg"))
        private const val SPLASH_WIDTH = 1000
        private const val SPLASH_HEIGHT = 480

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

                    if (e.code == KeyCode.H) {
                        mainController.fitScreenAction(null, 0.50)
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
            launch(Main::class.java, *args)
        }
    }
}
