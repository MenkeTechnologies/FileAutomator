package mainPackage

import com.kodedu.terminalfx.TerminalBuilder
import com.kodedu.terminalfx.config.TerminalConfig
import com.sun.management.OperatingSystemMXBean
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.application.Platform
import javafx.beans.InvalidationListener
import javafx.beans.binding.Bindings
import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.Initializable
import javafx.geometry.Orientation
import javafx.geometry.Point2D
import javafx.geometry.Point3D
import javafx.geometry.Rectangle2D
import javafx.scene.Cursor
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.PointLight
import javafx.scene.control.*
import javafx.scene.effect.Reflection
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.*
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.scene.media.MediaView
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Cylinder
import javafx.scene.shape.DrawMode
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.TextFlow
import javafx.scene.web.WebView
import javafx.stage.DirectoryChooser
import javafx.stage.Screen
import javafx.util.Duration
import org.controlsfx.control.textfield.AutoCompletionBinding
import org.controlsfx.control.textfield.TextFields
import java.awt.Desktop
import java.io.File
import java.io.IOException
import java.lang.management.ManagementFactory
import java.net.URL
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.prefs.Preferences
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainController : Initializable {
    @JvmField var mainTableView: TableView<FileInfo> = TableView()
    @JvmField var mainTextField: TextField = TextField()
    @JvmField var directoryToSearchTextField: TextField = TextField()
    @JvmField var mainSplitPane: SplitPane = SplitPane()
    @JvmField var topTilePane: SplitPane = SplitPane()
    @JvmField var bottomTilePane: SplitPane = SplitPane()
    @JvmField var sizeLabel: Label = Label()
    @JvmField var lastModifiedLabel: Label = Label()
    @JvmField var pathLabel: Label = Label()
    @JvmField var destinationCopyAllTextField: TextField = TextField()
    @JvmField var fileNamDetailLabel: Label = Label()
    @JvmField var caseInsensitiveMatchingCheckbox: ToggleButton = ToggleButton()
    @JvmField var fileBrowserTreeTable: TreeView<Any> = TreeView()
    @JvmField var automaticSearchCheckBox: ToggleButton = ToggleButton()
    @JvmField var showHiddenFilesCheckBox: ToggleButton = ToggleButton()
    @JvmField var selectInTreeViewCheckBox: ToggleButton = ToggleButton()
    @JvmField var hideDirectoriesCheckBox: ToggleButton = ToggleButton()
    @JvmField var pathMatchingCheckbox: ToggleButton = ToggleButton()
    @JvmField var rightPaneImageView: ImageView = ImageView()
    @JvmField var rightSidePaneVBox: VBox = VBox()
    @JvmField var rightPaneScrollPane: ScrollPane = ScrollPane()
    @JvmField var fileNamDetailLabelContent: Label = Label()
    @JvmField var sizeDetailLabelContent: Label = Label()
    @JvmField var pathLabelContent: Label = Label()
    @JvmField var lastModifiedLabelContent: Label = Label()
    @JvmField var textFlowRightPane: TextFlow = TextFlow()
    @JvmField var textLabel: Label = Label()
    @JvmField var textContent: CodeTextArea? = null
    @JvmField var rightPaneMediaView: MediaView = MediaView()
    @JvmField var menuBar: MenuBar = MenuBar()
    @JvmField var thinkingIndicator: ProgressIndicator = ProgressIndicator()
    @JvmField var activityIndicatorLabel: Label = Label()
    @JvmField var stopCurrentSearchButton: Button = Button()
    @JvmField var searchButton: Button = Button()
    @JvmField var progressIndicator: ProgressBar = ProgressBar()
    @JvmField var playPositionSlider: Slider = Slider()
    @JvmField var autoplayCheckbox: ToggleButton = ToggleButton()
    @JvmField var mediaPlayerControls: FlowPane = FlowPane()
    @JvmField var play2XSlowerButton: Button = Button()
    @JvmField var play2XFasterButton: Button = Button()
    @JvmField var mediaPlayerRateLabel: Label = Label()
    @JvmField var mediaStackPane: StackPane = StackPane()
    @JvmField var volumeSlider: Slider = Slider()
    @JvmField var playMediaButton: Button = Button()
    @JvmField var currentTimeLabel: Label = Label()
    @JvmField var totalTimeLabel: Label = Label()
    @JvmField var loopButton: ToggleButton = ToggleButton()
    @JvmField var sliderHbox: HBox = HBox()
    @JvmField var fullScreenMediaButton: Button = Button()
    @JvmField var topHBox: HBox = HBox()
    @JvmField var bottomHBox: HBox = HBox()
    @JvmField var rightSidePaneTextVBox: VBox = VBox()
    @JvmField var topSecondHBox: HBox = HBox()
    @JvmField var lockMediaViewMediaControlsToggle: ToggleButton = ToggleButton()
    @JvmField var numberResultsLabel: Label = Label()
    @JvmField var loadingFileLabel: Label = Label()
    @JvmField var volumeAndCurrentTimeSwipeLabel: Label = Label()
    @JvmField var autoPlayMediaControl: ToggleButton = ToggleButton()
    @JvmField var fileNameLabelMediaControls: Label = Label()
    @JvmField var normalScreenMediaButton: Button = Button()
    @JvmField var removeSliderMediaControl: ToggleButton = ToggleButton()
    @JvmField var filterHBox: HBox = HBox()
    @JvmField var menuButtonAdd: MenuButton = MenuButton()
    @JvmField var sphere: Cylinder = Cylinder()
    @JvmField var pointLight: PointLight = PointLight()
    @JvmField var imagesVBox: VBox = VBox()
    @JvmField var showPlayingIconCheckbox: ToggleButton = ToggleButton()
    @JvmField var showPlayingIconTreeCheckbox: ToggleButton = ToggleButton()
    @JvmField var showLineNumbersCheckbox: ToggleButton = ToggleButton()
    @JvmField var showPlayinIconTreeHBox: HBox = HBox()
    @JvmField var showReflectionBottomButton: ToggleButton = ToggleButton()
    @JvmField var showReflectionButton: ToggleButton = ToggleButton()
    @JvmField var lockMediaViewBottomToggle: ToggleButton = ToggleButton()
    @JvmField var fitScreenToggleButton: ToggleButton = ToggleButton()
    @JvmField var fitScreenToggleMediaButton: ToggleButton = ToggleButton()
    @JvmField var terminalTabPane: TabPane = TabPane()
    @JvmField var systemStatsLabel: Label = Label()
    @JvmField var webViewTextField: TextField = TextField()
    @JvmField var webViewBackButton: Button = Button()
    @JvmField var webViewForwardButton: Button = Button()
    @JvmField var splitPaneWebView: WebView = WebView()
    @JvmField var restorePanesButton: Button = Button()
    @JvmField var textFlowFinalFinalRightPane: TextFlow = TextFlow()
    @JvmField var webViewVBox: VBox = VBox()
    @JvmField var mediaPlayerRateProperty: DoubleProperty = SimpleDoubleProperty(1.0)
    @JvmField var mediaPlayerVolumeProperty: DoubleProperty = SimpleDoubleProperty(1.0)

    @JvmField var files: ObservableList<FileInfo> = FXCollections.observableArrayList()
    @JvmField var root: TreeItem<*>? = null
    @JvmField var out = false
    @JvmField var hidden = false
    @JvmField var dividerPositions = arrayOf(0.0, 0.0)
    @JvmField var currentlySelectedFilePathTreeItem: FilePathTreeItem? = null
    @JvmField var timeline: Timeline? = null
    @JvmField var disappearTimer: Timer? = null
    @JvmField var filesForAutoplay: ArrayList<String> = ArrayList()
    @JvmField var splitPaneChildren: ObservableList<Node> = FXCollections.observableArrayList()
    @JvmField var topTileChildren: ObservableList<Node> = FXCollections.observableArrayList()
    @JvmField var bottomTileChildren: ObservableList<Node> = FXCollections.observableArrayList()
    @JvmField var binding: AutoCompletionBinding<String>? = null
    @JvmField var contextMenu: ContextMenu? = null
    @JvmField var oldScreenSize: Rectangle2D? = null
    @JvmField var points: ArrayList<Point2D> = ArrayList()
    @JvmField var counter: Int = 0
    @JvmField var halfToggle = true

    private var monitoringExecutor: ScheduledExecutorService? = null

    init {
        val rect = Screen.getPrimary().bounds
        points.add(Point2D(rect.minX, rect.minY))
        points.add(Point2D(rect.minX, rect.maxY / 2))
        points.add(Point2D(rect.maxX / 2, rect.maxY / 2))
        points.add(Point2D(rect.maxX / 2, rect.minY))
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        addWebViewPane()
        addTerminalPane()

        mediaPlayer = MediaPlayer(Media(javaClass.getResource("/ClosedHH.wav")!!.toExternalForm()))

        val scalingFactor = 7.0

        sphere = Cylinder(2 * scalingFactor, 2 * scalingFactor)
        val material = PhongMaterial(Color.BLACK)
        material.specularColor = Color.LIGHTBLUE
        material.specularPower = 10.0
        sphere.material = material

        pointLight = PointLight(Color.WHITE)
        sphere.material = material

        val translateY = SimpleDoubleProperty()

        timeline = Timeline(
            KeyFrame(Duration.seconds(0.0), KeyValue(translateY, 3000)),
            KeyFrame(Duration.seconds(10.0), KeyValue(translateY, 0))
        )

        sphere.drawMode = DrawMode.LINE
        sphere.rotationAxis = Point3D(3.0, 1.0, 1.0)
        sphere.rotateProperty().bind(translateY)
        sphere.translateZ = 90.0

        timeline!!.isAutoReverse = true
        timeline!!.cycleCount = Animation.INDEFINITE

        val group = Group(sphere, pointLight)
        group.prefWidth(100.0)
        group.style = "-fx-background-color: red"

        topHBox.children.add(group)

        menuBar.isUseSystemMenuBar = true
        mainTableView.columns.clear()

        rightPaneMediaView.mediaPlayer = mediaPlayer

        TreeViewInitialization.initTreeView(this)

        mainSplitPane.setDividerPositions(0.5)
        topTilePane.setDividerPositions(0.33, 0.66)
        bottomTilePane.setDividerPositions(0.33, 0.66)

        mainTableView.isEditable = false
        mainTableView.items = files

        TableViewUtilities.initTableViewColumns(mainTableView, this)

        mainTableView.addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
            if (e.isSecondaryButtonDown) {
                e.consume()
            }
        }

        fileBrowserTreeTable.addEventFilter(MouseEvent.MOUSE_PRESSED) { e ->
            if (e.isSecondaryButtonDown) {
                e.consume()
            }
        }

        mainTableView.setOnMouseClicked { e ->
            Utilities.fromAutoPlay = false

            if (e.button == MouseButton.PRIMARY) {
                val item = mainTableView.selectionModel.selectedItem

                if (item != null) {
                    if (lockMediaViewMediaControlsToggle.isSelected) {
                        startPlayingMedia(item, false, false)
                    } else {
                        startPlayingMedia(item, true, false)
                    }
                }
            }
        }

        fileBrowserTreeTable.setOnMouseClicked { e ->
            if (e.button == MouseButton.PRIMARY) {
                val item = fileBrowserTreeTable.selectionModel.selectedItem

                if (!lockMediaViewMediaControlsToggle.isSelected) {
                    startPlayingMediaFromTree(item)
                }
            }
        }

        rightPaneScrollPane.setOnMouseClicked { e ->
            if (e.button == MouseButton.SECONDARY) {
                val list = mainTableView.items
                val cm = Utilities.createContextMenu(FileInfo(pathLabelContent.text), mainTableView, list, this, "stackPane")
                cm.show(mediaStackPane.scene.window, e.sceneX, e.sceneY)
            }
        }

        splitPaneChildren.addAll(mainSplitPane.items)
        topTileChildren.addAll(topTilePane.items)
        bottomTileChildren.addAll(bottomTilePane.items)

        restorePanesButton.setOnAction { restorePanesToOld(null) }

        DraggingInit.initDraggingBindings(this)
        TileDragManager.initTileDragging(this)

        initCheckBoxes()
        initTasks()
        initToolTips()
        initUsageMonitoring()
        initFileSystemChangesMonitoring()
        initAutoComplete()
    }

    fun addWebViewPane() {
        splitPaneWebView.prefHeightProperty().bind(bottomTilePane.heightProperty())
        splitPaneWebView.isContextMenuEnabled = false

        splitPaneWebView.setOnMouseClicked { event ->
            if (event.button == MouseButton.SECONDARY) {
                val reload = MenuItem("Reload page")
                val closePaneWeb = MenuItem("Close this pane.")

                reload.setOnAction { splitPaneWebView.engine.reload() }
                closePaneWeb.setOnAction { removePaneSingular("webView") }

                if (contextMenu != null) {
                    contextMenu!!.hide()
                }

                contextMenu = ContextMenu()
                contextMenu!!.items.addAll(reload, closePaneWeb)
                contextMenu!!.show(splitPaneWebView, event.screenX, event.screenY)
            } else {
                if (contextMenu != null) contextMenu!!.hide()
            }
        }

        webViewBackButton.setOnAction { splitPaneWebView.engine.history.go(-1) }
        webViewForwardButton.setOnAction { splitPaneWebView.engine.history.go(1) }

        webViewTextField.text = "http://www.youtube.com"
        webViewTextField.setOnAction { splitPaneWebView.engine.load(webViewTextField.text) }
    }

    private fun initAutoComplete() {
        val autoCompleteDirectoriesArrayList = ArrayList<String>()

        val autoCompleteHandler = EventHandler<KeyEvent> { e ->
            when (e.code) {
                KeyCode.SLASH, KeyCode.BACK_SLASH, KeyCode.BACK_SPACE,
                KeyCode.DOWN, KeyCode.UP, KeyCode.LEFT, KeyCode.RIGHT -> {}
                else -> {
                    val tf = e.source as TextField
                    val possiblePath = File(tf.text)

                    if (possiblePath.exists()) {
                        autoCompleteDirectoriesArrayList.clear()

                        try {
                            Files.walkFileTree(Paths.get(possiblePath.absolutePath), HashSet(), 1, object : FileVisitor<Path> {
                                override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = FileVisitResult.CONTINUE
                                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                                    if (file.toFile().isDirectory) {
                                        autoCompleteDirectoriesArrayList.add(file.toFile().absolutePath)
                                    }
                                    return FileVisitResult.CONTINUE
                                }
                                override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult = FileVisitResult.SKIP_SUBTREE
                                override fun postVisitDirectory(dir: Path, exc: IOException?): FileVisitResult = FileVisitResult.CONTINUE
                            })

                            if (binding != null) {
                                binding!!.dispose()
                            }

                            System.err.println("___________${Thread.currentThread().stackTrace[1].className}____Line:${Thread.currentThread().stackTrace[1].lineNumber}___ $binding")

                            binding = TextFields.bindAutoCompletion(directoryToSearchTextField, autoCompleteDirectoriesArrayList)
                            binding!!.minWidth = 500.0
                            binding!!.setHideOnEscape(true)
                            // binding delay not accessible directly

                            binding!!.setOnAutoCompleted {
                                binding!!.dispose()
                            }
                        } catch (e1: IOException) {
                            e1.printStackTrace()
                        }
                    }
                }
            }
        }

        directoryToSearchTextField.onKeyReleased = autoCompleteHandler
        destinationCopyAllTextField.onKeyReleased = autoCompleteHandler
    }

    private fun initFileSystemChangesMonitoring() {
        try {
            val watchService = FileSystems.getDefault().newWatchService()

            Thread {
                try {
                    Paths.get(directoryToSearchTextField.text).register(
                        watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY
                    )

                    var watchKey = watchService.take()

                    do {
                        watchKey.pollEvents().forEach { e ->
                            System.err.println("___________${Thread.currentThread().stackTrace[1].className}____Line:${Thread.currentThread().stackTrace[1].lineNumber}___ ${e.context()}")
                        }
                    } while (watchKey.reset())
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initUsageMonitoring() {
        val operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean() as OperatingSystemMXBean

        monitoringExecutor = Executors.newSingleThreadScheduledExecutor { r ->
            val t = Thread(r, "usage-monitor")
            t.isDaemon = true
            t
        }

        monitoringExecutor!!.scheduleAtFixedRate({
            val memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
            val text = String.format("CPU %.2f%% MEM %s", operatingSystemMXBean.processCpuLoad * 100, PortableFileUtilities.turnBytesIntoHumanReadable(memUsed))
            Platform.runLater { systemStatsLabel.text = text }
        }, 1, 1, TimeUnit.SECONDS)
    }

    fun addTerminalPane() {
        val darkConfig = TerminalConfig()
        darkConfig.setBackgroundColor(Color.rgb(16, 16, 16))
        darkConfig.setForegroundColor(Color.rgb(240, 240, 240))
        darkConfig.setCursorColor(Color.rgb(255, 0, 0, 0.5))

        val terminalBuilder1 = TerminalBuilder(darkConfig)
        val terminalTab = terminalBuilder1.newTerminal()

        terminalTabPane = TabPane()
        terminalTabPane.tabs.addAll(terminalTab)

        val maximizeThisPane = MenuItem("Maximize This Pane")
        val hideThisPane = MenuItem("Hide This Pane")
        val restorePanes = MenuItem("Restore Panes")

        terminalTab.contextMenu.items.addAll(maximizeThisPane, hideThisPane, restorePanes)

        maximizeThisPane.setOnAction { removePanes(terminalTabPane) }
        hideThisPane.setOnAction { removePaneSingular("terminal") }
        restorePanes.setOnAction { restorePanesToOriginal("terminal") }

        bottomTilePane.items.add(terminalTabPane)
    }

    fun initTreeViewKeyBindings() {
        fileBrowserTreeTable.onKeyReleased = EventHandler { e ->
            if (e.code == KeyCode.DOWN || e.code == KeyCode.UP) {
                val item = fileBrowserTreeTable.selectionModel.selectedItem
                if (!lockMediaViewMediaControlsToggle.isSelected) {
                    startPlayingMediaFromTree(item)
                }
            }
        }
    }

    fun initMainTableViewKeyBindings() {
        mainTableView.onKeyReleased = EventHandler { e ->
            Utilities.fromAutoPlay = false

            if (e.code == KeyCode.DOWN || e.code == KeyCode.UP) {
                val item = mainTableView.selectionModel.selectedItem
                if (lockMediaViewMediaControlsToggle.isSelected) {
                    startPlayingMedia(item, false, false)
                } else {
                    startPlayingMedia(item, true, false)
                }
            }
        }
    }

    private fun initToolTips() {}

    fun startPlayingMediaFromTree(item: Any?) {
        val filePathTreeItem = item as FilePathTreeItem

        currentlySelectedFilePathTreeItem = filePathTreeItem

        if (filePathTreeItem.getPathString() != null) {
            val file = FileInfo(filePathTreeItem.getPathString())
            updateRightSidePane(file)

            runInBackgroundThreadSecondary(Runnable {
                Utilities.updateThumbnailRightSidePane(this@MainController, filePathTreeItem)
            })

            if (showPlayingIconTreeCheckbox.isSelected) {
                FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(filePathTreeItem.getPathString()), true)
            }
        }
    }

    fun startPlayingMedia(item: Any?, playInRightPane: Boolean, fromContext: Boolean) {
        val fileInfo = item as FileInfo

        val filePathTreeItem = FilePathTreeItem(Paths.get(fileInfo.absolutePath), this@MainController)
        currentlySelectedFilePathTreeItem = filePathTreeItem

        if (selectInTreeViewCheckBox.isSelected) {
            if (!fromContext) {
                if (!Utilities.fromAutoPlay) {
                    runInBackgroundThreadSecondary(Runnable {
                        FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(fileInfo.absolutePath), true)
                    })
                }
            }
        }

        if (playInRightPane) {
            if (showPlayingIconTreeCheckbox.isSelected) {
                runInBackgroundThreadSecondary(Runnable {
                    FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(fileInfo.absolutePath), true)
                })
            }
            updateRightSidePane(fileInfo)

            runInBackgroundThreadSecondary(Runnable {
                Utilities.updateThumbnailRightSidePane(this@MainController, filePathTreeItem)
            })
        }
    }

    private fun initTasks() {
        loadingTask = CustomTask(this)
        searchingTask = CustomTask(this)
    }

    fun searchForFile(actionEvent: ActionEvent?) {
        if (File(directoryToSearchTextField.text).exists()) {
            RegexUtilities.searchAndRefresh(this)
        } else {
            CommonUtilities.showErrorAlert("Directory \"${directoryToSearchTextField.text}\" does not exist.")
        }
    }

    fun checkToShowHiddenFiles(file: Path) {
        hidden = false

        file.iterator().forEachRemaining { path ->
            if (path.toFile().absoluteFile.isHidden) {
                hidden = true
            }
        }

        if (hidden) {
            if (showHiddenFilesCheckBox.isSelected) {
                checktoShowDirectories(file)
            }
        } else {
            checktoShowDirectories(file)
        }
    }

    private fun checktoShowDirectories(file: Path) {
        if (hideDirectoriesCheckBox.isSelected) {
            if (!file.toFile().isDirectory) {
                files.add(FileInfo(file.toString()))
            }
        } else {
            files.add(FileInfo(file.toString()))
        }
    }

    fun initBindings() {
        textContent = CodeTextArea(this)

        topTilePane.items.add(textContent!!.codeArea)

        fitScreenToggleButton.setOnAction { e -> fitScreenAction(e, 1.0) }
        fitScreenToggleMediaButton.setOnAction { e -> fitScreenAction(e, 1.0) }

        RegexUtilities.searchAndRefresh(this)
        rightPaneMediaView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty())
        rightPaneImageView.fitWidthProperty().bind(rightPaneScrollPane.widthProperty())
        stopCurrentSearchButton.visibleProperty().bind(sphere.visibleProperty())
        stopCurrentSearchButton.managedProperty().bind(sphere.managedProperty())

        activityIndicatorLabel.textProperty().bind(searchingTask.messageProperty())
        loadingFileLabel.textProperty().bind(loadingTask.messageProperty())

        fullScreenMediaButton.disableProperty().bind(Utilities.maximized)
        normalScreenMediaButton.disableProperty().bind(Utilities.maximized.not())
        fitScreenToggleMediaButton.selectedProperty().bindBidirectional(fitScreenToggleButton.selectedProperty())

        showReflectionBottomButton.isSelected = true

        showReflectionButton.selectedProperty().bindBidirectional(showReflectionBottomButton.selectedProperty())
        showReflection(null)

        stopCurrentSearchButton.setOnAction {
            searchingTask.future?.cancel(true)
            if (stopCurrentSearchButton.text == "Stop Load") {
                loadingTask.future?.cancel(true)
            }
        }

        lockMediaViewBottomToggle.selectedProperty().bindBidirectional(lockMediaViewMediaControlsToggle.selectedProperty())
        currentTimeLabel.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.15))

        val fontObjectProperty = SimpleObjectProperty(Font.font("Helvetica", FontWeight.BOLD, 10.0))

        currentTimeLabel.fontProperty().bind(fontObjectProperty)
        totalTimeLabel.fontProperty().bind(fontObjectProperty)

        volumeAndCurrentTimeSwipeLabel.fontProperty().bind(fontObjectProperty)
        volumeAndCurrentTimeSwipeLabel.effectProperty().bind(
            Bindings.`when`(showReflectionBottomButton.selectedProperty()).then(Reflection()).otherwise(null as Reflection?)
        )

        rightPaneScrollPane.widthProperty().addListener { _, _, newValue ->
            val scalingFactor = 0.03
            val startingValue = 5
            fontObjectProperty.set(Font.font("Arial", FontWeight.BOLD, newValue.toDouble() * scalingFactor + startingValue))
        }

        totalTimeLabel.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.15))
        playPositionSlider.prefWidthProperty().bind(rightPaneScrollPane.widthProperty().multiply(0.7))

        volumeSlider.valueProperty().set(1.0)
    }

    fun initMediaPlayerBindings(sender: String) {
        if (sender == "video" || sender == "music") {
            volumeAndCurrentTimeSwipeLabel.isVisible = false

            mediaStackPane.setOnScroll { e ->
                val scrollType = FileTypeUtilities.getFileType(pathLabelContent.text)
                if ("video" == scrollType || "music" == scrollType) {
                    if (Math.abs(e.deltaY) > 4) {
                        Utilities.swipeRight = false

                        val changeY = e.deltaY * -1
                        val scalingFactor = 0.005

                        val changeTo = changeY * scalingFactor + volumeSlider.value

                        if (changeTo < 1 || changeTo > 0) {
                            volumeSlider.valueProperty().set(changeTo)
                            volumeSlider.isValueChanging = true
                            volumeSlider.isValueChanging = false
                        }
                    }

                    if (mediaPlayer.currentTime.greaterThan(Duration.seconds(0.0)) && mediaPlayer.currentTime.lessThan(mediaPlayer.totalDuration.subtract(Duration.seconds(2.0)))) {
                        if (Math.abs(e.deltaX) > 4) {
                            Utilities.swipeRight = true
                            val changeX = e.deltaX
                            val scalingFactorX = 0.001
                            val changeToX = changeX * scalingFactorX

                            if (changeToX < 1 || changeToX > 0) {
                                volumeAndCurrentTimeSwipeLabel.isVisible = true
                                var newDuration = Duration.millis(changeToX * mediaPlayer.totalDuration.toMillis()).add(mediaPlayer.currentTime)
                                if (newDuration.toMillis() < 0) newDuration = Duration.ZERO
                                if (newDuration.greaterThan(mediaPlayer.totalDuration)) {
                                    newDuration = mediaPlayer.totalDuration
                                    mediaPlayer.pause()
                                }
                                mediaPlayer.seek(newDuration)
                            }
                            hideNodeAfterDelay(volumeAndCurrentTimeSwipeLabel)
                        }
                    }

                    e.consume()
                }
            }

            volumeSlider.valueChangingProperty().addListener { _, oldValue, newValue ->
                volumeAndCurrentTimeSwipeLabel.isVisible = true
                if (oldValue && !newValue) {
                    hideNodeAfterDelay(volumeAndCurrentTimeSwipeLabel)
                }
            }

            mediaPlayerVolumeProperty.bind(volumeSlider.valueProperty())

            mediaPlayerVolumeProperty.addListener(InvalidationListener { observable ->
                Utilities.swipeRight = false
                volumeAndCurrentTimeSwipeLabel.text = String.format("Volume: %.1f%%", (observable as DoubleProperty).get() * 100)
            })

            mediaPlayer.rateProperty().bind(mediaPlayerRateProperty)
            mediaPlayer.volumeProperty().bind(mediaPlayerVolumeProperty)

            val pauseIcon = ImageView(Image(javaClass.getResourceAsStream("/png/pause.png")))
            val playIcon = ImageView(Image(javaClass.getResourceAsStream("/png/play.png")))
            playMediaButton.graphicProperty().bind(
                Bindings.`when`(mediaPlayer.statusProperty().isEqualTo(MediaPlayer.Status.PLAYING))
                    .then(pauseIcon).otherwise(playIcon)
            )

            play2XFasterButton.setOnAction {
                if (mediaPlayerRateProperty.get() < 8) {
                    mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() + 0.25)
                    mediaPlayerRateLabel.text = Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).value
                }
            }

            play2XSlowerButton.setOnAction {
                if (mediaPlayerRateProperty.get() > 0) {
                    mediaPlayerRateProperty.set(mediaPlayerRateProperty.get() - 0.25)
                    mediaPlayerRateLabel.text = Bindings.format("%3.2fx", mediaPlayerRateProperty.get()).value
                }
            }

            mediaStackPane.setOnMouseEntered {
                val type = FileTypeUtilities.getFileType(pathLabelContent.text)
                if ("video" == type || "music" == type) {
                    mediaPlayerControls.isVisible = true
                    disappearTimer = Timer()
                    disappearTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            Platform.runLater { Utilities.removeFromView(mediaPlayerControls) }
                        }
                    }, 3 * 1000L)
                }
            }

            mediaStackPane.setOnMouseMoved {
                val type = FileTypeUtilities.getFileType(pathLabelContent.text)
                if ("video" == type || "music" == type) {
                    if (!mediaPlayerControls.isVisible) {
                        Utilities.addToView(mediaPlayerControls)
                    } else {
                        if (disappearTimer != null) {
                            disappearTimer!!.cancel()
                            disappearTimer!!.purge()
                            disappearTimer = Timer()
                            disappearTimer!!.schedule(object : TimerTask() {
                                override fun run() {
                                    Platform.runLater { Utilities.removeFromView(mediaPlayerControls) }
                                }
                            }, 3000L)
                        }
                    }
                }
            }

            mediaStackPane.setOnMouseExited {
                val type = FileTypeUtilities.getFileType(pathLabelContent.text)
                if ("video" == type || "music" == type) {
                    Utilities.removeFromView(mediaPlayerControls)
                }
                if (disappearTimer != null) {
                    disappearTimer!!.cancel()
                    disappearTimer!!.purge()
                }
            }

            playMediaButton.setOnAction {
                if (mediaPlayer.status == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.play()
                }
            }
        }
    }

    fun hideNodeAfterDelay(node: Node) {
        val t = Thread {
            try {
                Thread.sleep(3000)
                Platform.runLater { node.isVisible = false }
            } catch (e1: InterruptedException) {
                e1.printStackTrace()
            }
        }
        t.start()
    }

    fun updateRightSidePane(newValue: FileInfo?) {
        val fileInfo = newValue
        if (fileInfo != null) {
            fileNamDetailLabelContent.text = fileInfo.getFileName()
            sizeDetailLabelContent.text = PortableFileUtilities.turnBytesIntoHumanReadable(fileInfo.length())
            pathLabelContent.text = fileInfo.absolutePath
            lastModifiedLabelContent.text = Utilities.formatDate(fileInfo.lastModified())
        }

        if (showPlayingIconCheckbox.isSelected) {
            mainTableView.refresh()
        }
    }

    private fun initCheckBoxes() {
        automaticSearchCheckBox.setOnAction { RegexUtilities.searchAndRefresh(this) }
        showHiddenFilesCheckBox.setOnAction { RegexUtilities.searchAndRefresh(this) }
        hideDirectoriesCheckBox.setOnAction { RegexUtilities.searchAndRefresh(this) }
        pathMatchingCheckbox.setOnAction { RegexUtilities.searchAndRefresh(this) }
    }

    private fun refreshTreeViewFromBottom() {}

    fun chooseDir(actionEvent: ActionEvent?) {
        val dc = DirectoryChooser()
        val newDirToSearch = dc.showDialog(mainTableView.scene.window)
        directoryToSearchTextField.text = newDirToSearch.absolutePath
    }

    fun refreshTreeView(actionEvent: ActionEvent?) {
        val filePathTreeItem = fileBrowserTreeTable.selectionModel.selectedItem as FilePathTreeItem
        try {
            filePathTreeItem.populateSourceAndImmediateChildren(filePathTreeItem)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun collapseNodesRecursive(treeItem: TreeItem<*>) {
        treeItem.isExpanded = false
        treeItem.children.forEach { node ->
            collapseNodesRecursive(node)
        }
    }

    fun collapseAllTreeNodes(actionEvent: ActionEvent?) {
        runInBackgroundThreadSecondary(Runnable {
            fileBrowserTreeTable.root.isExpanded = false
            if (fileBrowserTreeTable.root.children.isNotEmpty()) {
                fileBrowserTreeTable.root.children.forEach { node ->
                    collapseNodesRecursive(node as TreeItem<*>)
                }
            }
        })
    }

    fun findDirectoryInTree(actionEvent: ActionEvent?) {
        runInBackgroundThreadSecondary(Runnable {
            if (directoryToSearchTextField.text != "") {
                val fileInfo = FileInfo(directoryToSearchTextField.text)

                if (fileInfo.exists()) {
                    FilePathTreeItem.selectTreeItemRecursively(this@MainController, Paths.get(fileInfo.absolutePath), true)
                } else {
                    val error = "Not A Valid File or Folder."
                    CommonUtilities.showErrorAlert(error)
                }
            }
        })
    }

    fun runInBackgroundThread(r: Runnable) {
        Utilities.addToView(sphere)
        timeline!!.play()
        println("visible")
        stopCurrentSearchButton.text = "Stop Search"

        searchingTask = CustomTask(this, r, false)
        activityIndicatorLabel.textProperty().bind(searchingTask.messageProperty())

        searchButton.disableProperty().bind(searchingTask.runningProperty())

        searchingTask.setRunnable(r)

        val thread = Thread(searchingTask)
        thread.isDaemon = true
        thread.start()
    }

    fun runInBackgroundThreadSecondary(r: Runnable) {
        if (searchingTask.state != javafx.concurrent.Worker.State.RUNNING) {
            Utilities.addToView(sphere)
            timeline!!.play()
            stopCurrentSearchButton.text = "Stop Load"
            loadingTask = CustomTask(this, r, false)
        } else {
            loadingTask = CustomTask(this, r, true)
        }

        loadingFileLabel.textProperty().bind(loadingTask.messageProperty())

        val thread = Thread(loadingTask)
        thread.isDaemon = true
        thread.start()
    }

    fun goToHomeDirectory(actionEvent: ActionEvent?) {
        val home = System.getProperty("user.home")
        runInBackgroundThreadSecondary(Runnable {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true)
        })
    }

    fun copyAllButtonAction(actionEvent: ActionEvent?) {
        Utilities.copyFilesFromTableViewToDirectory(mainTableView, destinationCopyAllTextField.text)
    }

    fun openFromRightSidePane(actionEvent: ActionEvent?) {
        try {
            Desktop.getDesktop().open(File(pathLabelContent.text))
        } catch (e: IOException) {
            CommonUtilities.showErrorAlert("Could not open file.")
        }
    }

    fun copyPath(actionEvent: ActionEvent?) {
        CommonUtilities.copyToClipboard(pathLabelContent.text)
    }

    fun openEnclosing(actionEvent: ActionEvent?) {
        CommonUtilities.openEnclosingDirectory(FileInfo(pathLabelContent.text))
    }

    fun moveToTrash(actionEvent: ActionEvent?) {
        CommonUtilities.moveToTrash(FileInfo(pathLabelContent.text), mainTableView, files, this@MainController)
    }

    fun goToDesktopDirectory(actionEvent: ActionEvent?) {
        val home = System.getProperty("user.home") + File.separator + "Desktop"
        runInBackgroundThreadSecondary(Runnable {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true)
        })
    }

    fun openDestinationFolder(actionEvent: ActionEvent?) {
        val newFile = File(destinationCopyAllTextField.text)
        if (newFile.exists()) {
            try {
                Desktop.getDesktop().open(newFile)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            CommonUtilities.showErrorAlert("Destination directory does not exist.")
        }
    }

    fun maximizeVideo(actionEvent: ActionEvent?) {
        removePanes(rightPaneScrollPane)
    }

    fun returnToOldDividers(actionEvent: ActionEvent?) {
        restorePanesToOriginal("mediaContextMenu")
    }

    fun removePaneSingular(pane: String) {
        val totalItems = topTilePane.items.size + bottomTilePane.items.size

        if (totalItems <= 1) {
            CommonUtilities.showErrorAlert("Cannot Remove Last Pane")
        } else {
            checkForSender(pane)
            // Remove empty tile rows from the outer split pane
            if (topTilePane.items.isEmpty()) mainSplitPane.items.remove(topTilePane)
            if (bottomTilePane.items.isEmpty()) mainSplitPane.items.remove(bottomTilePane)
        }
    }

    fun checkForSender(pane: String) {
        when (pane) {
            "tableView" -> topTilePane.items.remove(mainTableView)
            "treeView" -> topTilePane.items.remove(fileBrowserTreeTable)
            "terminal" -> bottomTilePane.items.remove(terminalTabPane)
            "webView" -> bottomTilePane.items.remove(webViewVBox)
            else -> bottomTilePane.items.remove(rightPaneScrollPane)
        }
    }

    fun removePanes(node: Node) {
        splitPaneChildren.clear()
        splitPaneChildren.addAll(mainSplitPane.items)
        topTileChildren.clear()
        topTileChildren.addAll(topTilePane.items)
        bottomTileChildren.clear()
        bottomTileChildren.addAll(bottomTilePane.items)

        val totalItems = topTilePane.items.size + bottomTilePane.items.size

        if (totalItems == 1) {
            CommonUtilities.showErrorAlert("Already Maximized.")
        } else {
            saveDividerPositions()

            topTilePane.items.clear()
            bottomTilePane.items.clear()
            mainSplitPane.items.clear()

            topTilePane.items.add(node)
            mainSplitPane.items.add(topTilePane)

            if (node == rightPaneScrollPane) {
                val fileInfo = FileInfo(pathLabelContent.text)
                val type = FileTypeUtilities.getFileType(fileInfo.absolutePath)
                Utilities.removeFromView(topHBox)
                Utilities.removeFromView(bottomHBox)
                Utilities.removeFromView(topSecondHBox)

                if (type == "video" || type == "music" || type == "image" || type == "pdf") {
                    Utilities.addToView(fileNameLabelMediaControls)
                    Utilities.removeFromView(rightSidePaneTextVBox)
                }
            }

            Utilities.maximized.set(true)
        }
    }

    private fun saveDividerPositions() {
        for (i in 0 until topTilePane.dividers.size) {
            Preferences.userRoot().putDouble("topDividerPos$i", topTilePane.dividerPositions[i])
        }
        for (i in 0 until bottomTilePane.dividers.size) {
            Preferences.userRoot().putDouble("bottomDividerPos$i", bottomTilePane.dividerPositions[i])
        }
        for (i in 0 until mainSplitPane.dividers.size) {
            Preferences.userRoot().putDouble("mainDividerPos$i", mainSplitPane.dividerPositions[i])
        }
    }

    private fun restoreDividerPositions() {
        for (i in 0 until topTilePane.dividers.size) {
            topTilePane.setDividerPosition(i, Preferences.userRoot().getDouble("topDividerPos$i", if (topTilePane.dividerPositions.isNotEmpty()) topTilePane.dividerPositions[i] else 0.5))
        }
        for (i in 0 until bottomTilePane.dividers.size) {
            bottomTilePane.setDividerPosition(i, Preferences.userRoot().getDouble("bottomDividerPos$i", if (bottomTilePane.dividerPositions.isNotEmpty()) bottomTilePane.dividerPositions[i] else 0.5))
        }
        for (i in 0 until mainSplitPane.dividers.size) {
            mainSplitPane.setDividerPosition(i, Preferences.userRoot().getDouble("mainDividerPos$i", if (mainSplitPane.dividerPositions.isNotEmpty()) mainSplitPane.dividerPositions[i] else 0.5))
        }
    }

    fun restorePanesToOld(mediaContextMenu: String?) {
        topTilePane.items.clear()
        topTilePane.items.addAll(topTileChildren)
        bottomTilePane.items.clear()
        bottomTilePane.items.addAll(bottomTileChildren)
        mainSplitPane.items.clear()
        mainSplitPane.items.addAll(splitPaneChildren)

        if (!topHBox.isVisible) {
            Utilities.addToView(topHBox)
            Utilities.addToView(bottomHBox)
            Utilities.addToView(topSecondHBox)
            Utilities.removeFromView(fileNameLabelMediaControls)
            if (!rightSidePaneTextVBox.isVisible) {
                Utilities.addToView(rightSidePaneTextVBox)
            }
        }

        restoreDividerPositions()

        Utilities.maximized.set(false)
    }

    fun restorePanesToOriginal(mediaControl: String) {
        val items = mainSplitPane.items

        if (!topHBox.isVisible) {
            Utilities.addToView(topHBox)
            Utilities.addToView(bottomHBox)
            Utilities.addToView(topSecondHBox)
            Utilities.removeFromView(fileNameLabelMediaControls)
            if (!rightSidePaneTextVBox.isVisible) {
                Utilities.addToView(rightSidePaneTextVBox)
            }
        }

        topTilePane.items.clear()
        topTilePane.items.addAll(listOf(fileBrowserTreeTable, mainTableView))
        bottomTilePane.items.clear()
        bottomTilePane.items.addAll(listOf(rightPaneScrollPane, terminalTabPane))
        mainSplitPane.items.clear()
        mainSplitPane.items.addAll(listOf(topTilePane, bottomTilePane))

        restoreDividerPositions()

        Utilities.maximized.set(false)
    }

    fun goToDownloadsDirectory(actionEvent: ActionEvent?) {
        val home = System.getProperty("user.home") + File.separator + "Downloads"
        runInBackgroundThreadSecondary(Runnable {
            FilePathTreeItem.selectTreeItemRecursively(this, Paths.get(home), true)
        })
    }

    fun rewindToStart(actionEvent: MouseEvent?) {
        if (mediaPlayer != null) {
            if (actionEvent!!.eventType == MouseEvent.MOUSE_CLICKED) {
                if (actionEvent.button == MouseButton.PRIMARY) {
                    if (actionEvent.clickCount == 1) {
                        mediaPlayer.stop()
                        mediaPlayer.seek(Duration.ZERO)
                        mediaPlayer.pause()
                        mediaPlayer.play()
                    } else {
                        Utilities.endOfMediaAction(this, false)
                    }
                }
            }
        }
    }

    fun removeSliderHBox(actionEvent: ActionEvent?) {
        if (removeSliderMediaControl.isSelected) {
            Utilities.removeFromView(sliderHbox)
        } else {
            Utilities.addToView(sliderHbox)
        }
    }

    fun addFilter(actionEvent: ActionEvent?) {
        val menuItem = actionEvent!!.source as MenuItem
        val label = Label()

        when (menuItem.text) {
            "+ Or" -> label.text = "OR"
            "+ And" -> label.text = "AND"
        }

        val textField = TextField()
        textField.prefWidth = 40.0

        val hBox = HBox()
        hBox.children.addAll(label, textField)
        filterHBox.children.add(hBox)

        label.setOnDragDetected { event ->
            val db = label.startDragAndDrop(*TransferMode.ANY)
            mainTableView.scene.cursor = Cursor.CLOSED_HAND
            val clipboardContent = ClipboardContent()
            clipboardContent.putString(filterHBox.children.indexOf(hBox).toString())
            db.setContent(clipboardContent)
            event.consume()
        }

        fileBrowserTreeTable.setOnDragOver { e ->
            if (e.gestureSource != mainTableView && e.dragboard.hasString()) {
                e.acceptTransferModes(*TransferMode.ANY)
                mainTableView.scene.cursor = Cursor.DISAPPEAR
            }
            e.consume()
        }

        fileBrowserTreeTable.setOnDragEntered { e ->
            if (e.source != mainTableView) {
                mainTableView.scene.cursor = Cursor.DISAPPEAR
            }
            e.consume()
        }

        fileBrowserTreeTable.setOnDragExited { e ->
            mainTableView.scene.cursor = Cursor.CLOSED_HAND
            e.consume()
        }

        fileBrowserTreeTable.setOnDragDropped { e ->
            val db = e.dragboard
            if (db.hasString()) {
                val integer = Integer.parseInt(db.string)
                filterHBox.children.removeAt(integer)
            }
            e.isDropCompleted = true
            mainTableView.scene.cursor = Cursor.DEFAULT
            e.consume()
        }

        label.setOnDragDone { e ->
            println("done with drag and drop")
            e.consume()
        }
    }

    fun removeFilter(actionEvent: ActionEvent?) {
        if (filterHBox.children.size > 1) {
            filterHBox.children.removeAt(filterHBox.children.size - 1)
        }
    }

    fun storeFileList(actionEvent: ActionEvent?) {
        if (autoplayCheckbox.isSelected) {
            mainTableView.refresh()
            filesForAutoplay.clear()

            for (i in 0 until mainTableView.items.size) {
                val file = mainTableView.items[i]
                filesForAutoplay.add(file.absolutePath)
            }
        }
    }

    fun showPlayingIcon(actionEvent: ActionEvent?) {
        mainTableView.refresh()
    }

    fun showLineNumbers(actionEvent: ActionEvent?) {
        if (pathLabelContent != null) {
            val filePathTreeItem = FilePathTreeItem(Paths.get(pathLabelContent.text), this)
            if (filePathTreeItem.isTextual()) {
                Utilities.updateThumbnailRightSidePane(this, filePathTreeItem)
            }
        }
    }

    fun showReflection(actionEvent: ActionEvent?) {
        if (showReflectionBottomButton.isSelected) {
            Utilities.initEffects(this.rightPaneMediaView)
            Utilities.initEffects(this.rightPaneImageView)
            Utilities.initEffects(this.playPositionSlider)
            Utilities.initEffects(this.mediaPlayerControls)
        } else {
            Utilities.removeEffects(this.rightPaneMediaView)
            Utilities.removeEffects(this.rightPaneImageView)
            Utilities.removeEffects(this.playPositionSlider)
            Utilities.removeEffects(this.mediaPlayerControls)
        }
    }

    fun playNext(actionEvent: ActionEvent?) {
        if (mediaPlayer != null) {
            mediaPlayer.stop()
            mediaPlayer.play()
            Utilities.endOfMediaAction(this, true)
        }
    }

    fun showPlayingIconTree(actionEvent: ActionEvent?) {
        if (showPlayingIconTreeCheckbox.isSelected) {
            fileBrowserTreeTable.refresh()
        }

        val fileInfo = FileInfo(pathLabelContent.text)
        runInBackgroundThreadSecondary(Runnable {
            FilePathTreeItem.selectTreeItemRecursivelyAndChangeGraphic(this, Paths.get(fileInfo.absolutePath), true)
        })
    }

    fun fitScreenAction(actionEvent: ActionEvent?, size: Double) {
        if (size == 0.25) {
            val rect = Screen.getPrimary().bounds

            val oldX = mainSplitPane.scene.window.x
            val oldY = mainSplitPane.scene.window.y
            val height = mainSplitPane.scene.window.height
            val width = mainSplitPane.scene.window.width
            oldScreenSize = Rectangle2D(oldX, oldY, width, height)
            mainSplitPane.scene.window.height = rect.height / 2
            mainSplitPane.scene.window.width = rect.width / 2

            mainSplitPane.scene.window.x = points[counter % 4].x
            mainSplitPane.scene.window.y = points[counter % 4].y

            if (counter == 8) {
                counter = 0
            }
            counter++
        } else if (size == 0.5) {
            val rect = Screen.getPrimary().bounds

            val oldX = mainSplitPane.scene.window.x
            val oldY = mainSplitPane.scene.window.y
            val height = mainSplitPane.scene.window.height
            val width = mainSplitPane.scene.window.width

            oldScreenSize = Rectangle2D(oldX, oldY, width, height)
            mainSplitPane.scene.window.height = rect.height
            mainSplitPane.scene.window.width = rect.width / 2

            if (halfToggle) {
                mainSplitPane.scene.window.x = rect.minX
                mainSplitPane.scene.window.y = rect.minY
            } else {
                mainSplitPane.scene.window.x = rect.maxX / 2
                mainSplitPane.scene.window.y = rect.minY
            }
            halfToggle = !halfToggle
        } else if (size == 1.0) {
            if (fitScreenToggleButton.isSelected) {
                val rect = Screen.getPrimary().bounds

                val oldX = mainSplitPane.scene.window.x
                val oldY = mainSplitPane.scene.window.y
                val height = mainSplitPane.scene.window.height
                val width = mainSplitPane.scene.window.width

                oldScreenSize = Rectangle2D(oldX, oldY, width, height)

                mainSplitPane.scene.window.height = rect.height
                mainSplitPane.scene.window.width = rect.width
                mainSplitPane.scene.window.x = rect.minX
                mainSplitPane.scene.window.y = rect.minY
            } else {
                if (oldScreenSize != null) {
                    mainSplitPane.scene.window.height = oldScreenSize!!.height
                    mainSplitPane.scene.window.width = oldScreenSize!!.width
                    mainSplitPane.scene.window.x = oldScreenSize!!.minX
                    mainSplitPane.scene.window.y = oldScreenSize!!.minY
                }
            }
        }
    }

    fun changeOrientation(actionEvent: ActionEvent?) {
        if (mainSplitPane.orientation == Orientation.VERTICAL) {
            mainSplitPane.orientation = Orientation.HORIZONTAL
            topTilePane.orientation = Orientation.VERTICAL
            bottomTilePane.orientation = Orientation.VERTICAL
        } else {
            mainSplitPane.orientation = Orientation.VERTICAL
            topTilePane.orientation = Orientation.HORIZONTAL
            bottomTilePane.orientation = Orientation.HORIZONTAL
        }
    }

    companion object {
        @JvmField var searchingTask: CustomTask<String> = CustomTask()
        @JvmField var loadingTask: CustomTask<String> = CustomTask()
        @JvmStatic var mediaPlayer: MediaPlayer = MediaPlayer(Media(MainController::class.java.getResource("/ClosedHH.wav")!!.toExternalForm()))
        @JvmField var rasterizingTask: CustomTask<String>? = null
    }
}
