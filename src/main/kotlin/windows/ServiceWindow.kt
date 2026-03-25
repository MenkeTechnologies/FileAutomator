package windows

import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Service
import javafx.concurrent.Task
import javafx.concurrent.Worker
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import mainPackage.CommonUtilities
import mainPackage.FileInfo
import mainPackage.Utilities

class ServiceWindow(
    private val mainTableView: TableView<*>,
    private val destinationPathString: String
) : Stage() {

    private var numberOfFiles: Int = mainTableView.items.size
    private var root: BorderPane? = null
    private var startingFileNumber: Int = 1
    private val model: Model
    private val view: View

    init {
        model = Model()
        view = View()

        root!!.styleProperty().bind(mainTableView.scene.root.styleProperty())
        hookupEvents()
        scene = Scene(root, 550.0, 330.0)
        val myservice = model.worker as Service<*>
        myservice.start()

        myservice.setOnSucceeded {
            if (model.worker.progressProperty().isEqualTo(1).get()) {
                close()
            }
        }
    }

    private fun hookupEvents() {
        view.startButton.setOnAction {
            startingFileNumber = 1
            paused.set(false)
            (model.worker as Service<*>).restart()
        }

        view.cancelButton.setOnAction {
            model.worker.cancel()
        }

        view.pauseButton.setOnAction {
            if (!paused.get()) {
                paused.value = true
            } else {
                paused.value = false
                (model.worker as Service<*>).restart()
            }
        }
    }

    private inner class Model {
        val worker: Worker<String>
        val numberOfItems = SimpleIntegerProperty(numberOfFiles)

        init {
            worker = object : Service<String>() {
                override fun createTask(): Task<String> {
                    return object : Task<String>() {
                        override fun call(): String {
                            updateTitle("Processing $numberOfFiles files.")
                            updateMessage("Starting...")
                            updateProgress(0, numberOfFiles.toLong())
                            for (i in startingFileNumber..numberOfFiles) {
                                if (isCancelled) {
                                    updateValue("Canceled.")
                                }

                                try {
                                    if (paused.get()) {
                                        startingFileNumber = i
                                        break
                                    } else {
                                        copyFiles(this, i)
                                    }
                                    updateProgress(i.toLong(), numberOfFiles.toLong())
                                } catch (e: Exception) {
                                    if (isCancelled) {
                                        updateValue("Canceled.")
                                    }
                                    e.printStackTrace()
                                }
                            }
                            return "Completed."
                        }

                        private fun copyFiles(task: Task<String>, i: Int) {
                            val sourceFile = mainTableView.items[i - 1] as FileInfo
                            updateTitle("Processing ${sourceFile.getFileName()}")

                            val destinationPath = FileInfo(destinationPathString)
                            if (!destinationPath.exists()) {
                                updateMessage("Creating Directories.")
                                if (destinationPath.mkdir()) {
                                    Utilities.copyFile(sourceFile, destinationPath)
                                } else {
                                    Platform.runLater {
                                        CommonUtilities.showErrorAlert("Could not create directory.")
                                    }
                                }
                            } else {
                                updateMessage("Copying Files $i of $numberOfFiles files.")
                                Utilities.copyFile(sourceFile, destinationPath)
                            }
                            updateMessage("Processed $i of $numberOfFiles files.")
                        }
                    }
                }
            }
        }
    }

    private inner class View {
        val progressBar: ProgressBar
        val title: Label
        val message: Label
        val running: Label
        val state: Label
        val totalWork: Label
        val workDone: Label
        val progress: Label
        val value: Label
        val startButton: Button
        val cancelButton: Button
        val pauseButton: Button

        init {
            progressBar = ProgressBar()
            progressBar.minWidth = 250.0
            title = Label()
            message = Label()
            running = Label()
            state = Label()
            totalWork = Label()
            workDone = Label()
            progress = Label()
            value = Label()
            pauseButton = Button("Pause")
            startButton = Button("Restart")
            cancelButton = Button("Cancel")

            val stateProperty = model.worker.stateProperty()
            progressBar.progressProperty().bind(model.worker.progressProperty())

            title.textProperty().bind(model.worker.titleProperty())
            message.textProperty().bind(model.worker.messageProperty())
            running.textProperty().bind(model.worker.runningProperty().asString())
            state.textProperty().bind(model.worker.stateProperty().asString())
            totalWork.textProperty().bind(model.worker.totalWorkProperty().asString())
            workDone.textProperty().bind(model.worker.workDoneProperty().asString())
            progress.textProperty().bind(Bindings.format("%5.2f%%", model.worker.progressProperty().multiply(100)))
            value.textProperty().bind(model.worker.valueProperty())

            startButton.disableProperty().bind(stateProperty.isEqualTo(Worker.State.RUNNING))
            cancelButton.disableProperty().bind(stateProperty.isNotEqualTo(Worker.State.RUNNING))
            pauseButton.disableProperty().bind(model.worker.progressProperty().isEqualTo(1))
            pauseButton.textProperty().bind(Bindings.`when`(paused).then("Resume").otherwise("Pause"))

            val topPane = HBox(10.0, progressBar)
            topPane.padding = Insets(10.0, 10.0, 10.0, 10.0)
            topPane.alignment = Pos.CENTER

            val constraints1 = ColumnConstraints()
            constraints1.halignment = HPos.RIGHT
            constraints1.minWidth = 65.0

            val constraints2 = ColumnConstraints()
            constraints2.halignment = HPos.LEFT
            constraints2.minWidth = 200.0

            val centerPane = GridPane()
            centerPane.hgap = 10.0
            centerPane.vgap = 10.0
            centerPane.padding = Insets(10.0, 10.0, 10.0, 10.0)
            centerPane.columnConstraints.addAll(constraints1, constraints2)
            centerPane.add(Label("Title:"), 0, 0)
            centerPane.add(Label("Message:"), 0, 1)
            centerPane.add(Label("Running:"), 0, 2)
            centerPane.add(Label("State:"), 0, 3)
            centerPane.add(Label("Total Work:"), 0, 4)
            centerPane.add(Label("Work Done:"), 0, 5)
            centerPane.add(Label("Progress:"), 0, 6)
            centerPane.add(Label("Value:"), 0, 7)

            centerPane.add(title, 1, 0)
            centerPane.add(message, 1, 1)
            centerPane.add(running, 1, 2)
            centerPane.add(state, 1, 3)
            centerPane.add(totalWork, 1, 4)
            centerPane.add(workDone, 1, 5)
            centerPane.add(progress, 1, 6)
            centerPane.add(value, 1, 7)

            val buttonPane = HBox(10.0, startButton, cancelButton, pauseButton)
            buttonPane.padding = Insets(10.0, 10.0, 10.0, 10.0)
            buttonPane.alignment = Pos.CENTER

            val bp = BorderPane(centerPane, topPane, null, buttonPane, null)
            root = bp
        }
    }

    companion object {
        @JvmField
        val paused = SimpleBooleanProperty(false)
    }
}
