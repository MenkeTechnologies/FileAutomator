package mainPackage

import javafx.application.Platform
import javafx.concurrent.Task
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

open class CustomTask<T> : Task<String> {
    var r: Runnable? = null
    var mainController: MainController? = null
    var executorService: ExecutorService? = null
    var future: Future<*>? = null
    var searchingTaskRunning = false

    constructor(mainController: MainController, r: Runnable, b: Boolean) {
        this.mainController = mainController
        this.r = r
        this.searchingTaskRunning = b
    }

    constructor(mainController: MainController) {
        this.mainController = mainController
    }

    constructor()

    constructor(mainController: MainController, r: Runnable) {
        this.mainController = mainController
        this.r = r
    }

    fun getRunnable(): Runnable? = r
    fun setRunnable(r: Runnable) { this.r = r }

    public override fun updateMessage(message: String?) { super.updateMessage(message) }
    public override fun updateProgress(workDone: Double, max: Double) { super.updateProgress(workDone, max) }
    public override fun updateTitle(title: String?) { super.updateTitle(title) }
    public override fun updateValue(value: String?) { super.updateValue(value) }

    override fun call(): String {
        executorService = Executors.newSingleThreadExecutor()
        future = executorService!!.submit(r)

        try {
            future!!.get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: java.util.concurrent.ExecutionException) {
            e.printStackTrace()
        } finally {
            executorService!!.shutdownNow()
            executorService!!.awaitTermination(2, TimeUnit.SECONDS)
            if (!searchingTaskRunning) {
                Platform.runLater {
                    Utilities.removeFromView(mainController!!.sphere)
                    mainController!!.timeline?.stop()
                }
            }
        }
        return "Completed"
    }
}
