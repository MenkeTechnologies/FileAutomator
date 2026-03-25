package mainPackage

import javafx.application.Application
import javafx.application.Platform
import javafx.beans.property.SimpleIntegerProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import java.util.concurrent.Callable
import java.util.concurrent.FutureTask

class PromptingTask : Application() {
    override fun start(primaryStage: Stage) {
        val status = Label()
        val progress = ProgressBar()

        val textContainer = VBox(10.0)
        textContainer.style = "-fx-background-color: burlywood; -fx-padding: 10;"

        val task = LoadTextTask(SAMPLE_TEXT, textContainer)
        status.textProperty().bind(task.messageProperty())
        progress.progressProperty().bind(task.progressProperty())

        val taskThread = Thread(task, "label-generator")
        taskThread.isDaemon = true

        val layout = VBox(10.0)
        layout.children.addAll(status, progress, textContainer)
        layout.style = "-fx-background-color: cornsilk; -fx-padding: 10;"

        val imageView = ImageView(Image(javaClass.getResourceAsStream("../png/back.png")))
        layout.children.add(imageView)

        primaryStage.scene = Scene(layout, 300.0, 700.0)
        primaryStage.show()

        taskThread.start()
    }

    companion object {
        private val SAMPLE_TEXT =
            "MISSING Lorem ipsum dolor sit amet MISSING consectetur adipisicing elit sed do eiusmod tempor incididunt MISSING ut labore et dolore magna aliqua"
                .split(" ".toRegex()).toTypedArray()

        @JvmStatic
        fun main(args: Array<String>) {
            launch(PromptingTask::class.java, *args)
        }
    }
}

class LoadTextTask(
    private val lines: Array<String>,
    private val container: Pane
) : Task<Void?>() {
    private val idx = SimpleIntegerProperty(0)

    override fun call(): Void? {
        try {
            updateProgress(0, lines.size.toLong())

            while (idx.get() < lines.size) {
                val nextLabel = Label()
                val curIdx = idx.get()
                updateMessage("Reading Line: $curIdx")
                var nextText = lines[curIdx]

                if ("MISSING" == nextText) {
                    updateMessage("Prompting for missing text for line: $curIdx")
                    val futureTask = FutureTask(
                        MissingTextPrompt(container.scene.window)
                    )
                    Platform.runLater(futureTask)
                    nextText = futureTask.get()

                    nextLabel.style = "-fx-background-color: palegreen;"
                }
                nextLabel.text = nextText

                Platform.runLater(AddNodeLater(container, curIdx, nextLabel))
                idx.set(curIdx + 1)

                updateProgress((curIdx + 1).toLong(), lines.size.toLong())

                Thread.sleep(200)
            }

            updateMessage("Loading Text Completed: ${idx.get()} lines loaded.")
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

    inner class MissingTextPrompt(private val owner: Window) : Callable<String> {
        override fun call(): String {
            val dialog = Stage()
            dialog.title = "Enter Missing Text"
            dialog.initOwner(owner)
            dialog.initStyle(StageStyle.UTILITY)
            dialog.initModality(Modality.WINDOW_MODAL)

            val textField = TextField()
            val submitButton = Button("Submit")
            submitButton.isDefaultButton = true
            submitButton.setOnAction { dialog.close() }

            val layout = VBox(10.0)
            layout.alignment = Pos.CENTER_RIGHT
            layout.style = "-fx-background-color: azure; -fx-padding: 10;"
            layout.children.setAll(textField, submitButton)

            dialog.scene = Scene(layout)
            dialog.showAndWait()

            return textField.text
        }
    }

    inner class AddNodeLater(
        private val container: Pane,
        private val idx: Int,
        private val node: Node
    ) : Runnable {
        override fun run() {
            container.children.add(idx, node)
        }
    }
}
