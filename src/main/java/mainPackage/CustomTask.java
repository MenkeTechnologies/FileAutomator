package mainPackage;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by jacobmenke on 4/17/17.
 */
public class CustomTask<T> extends Task<String> {
    Runnable r;
    MainController mainController;
    ExecutorService executorService;
    Future future;
    boolean searchingTaskRunning = false;

    public CustomTask(MainController mainController, Runnable r, boolean b) {
        this.mainController = mainController;
        this.r = r;
        searchingTaskRunning = b;
    }

    public CustomTask(MainController mainController) {
        this.mainController = mainController;
    }

    public CustomTask() {
    }

    public CustomTask(MainController mainController, Runnable r) {
        this.mainController = mainController;
        this.r = r;
    }

    public Future getFuture() {
        return future;
    }

    public Runnable getRunnable() {
        return r;
    }

    public void setRunnable(Runnable r) {
        this.r = r;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void updateMessage(String message) {
        super.updateMessage(message);
    }

    @Override
    protected void updateProgress(double workDone, double max) {
        super.updateProgress(workDone, max);
    }

    @Override
    protected void updateTitle(String title) {
        super.updateTitle(title);
    }

    @Override
    protected void updateValue(String value) {
        super.updateValue(value);
    }

    @Override
    protected String call() throws Exception {

        executorService = Executors.newSingleThreadExecutor();
        future = executorService.submit(r);
        //System.out.println("called and submitting to executor");

        try {
            //System.out.println("waiting on future");
            future.get();
            //System.out.println("future done");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            if (!searchingTaskRunning) {
                Platform.runLater(() -> {
                    //      Utilities.removeFromView(mainController.thinkingIndicator);
                    Utilities.removeFromView(mainController.sphere);
                    mainController.timeline.stop();
                });
            }
        }

        executorService.shutdownNow();

        System.gc();

        return "Completed";
    }
}