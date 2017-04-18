package mainPackage;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.concurrent.*;

/**
 * Created by jacobmenke on 4/17/17.
 */
public class CustomTask<T> extends Task<String> {
    Runnable r;
    MainController mainController;
    ExecutorService executorService;
    Future future;

    public Future getFuture() {
        return future;
    }

    public Runnable getRunnable() {
        return r;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setRunnable(Runnable r) {
        this.r = r;
    }

    @Override
    protected void updateMessage(String message) {
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
        System.out.println("called and submitting to executor");

        try {

            future.get();
            System.out.println(mainController.mainTableView.getItems().size());

            CommonUtilities.FILE_COUNTER.set(0);

            System.out.println("invisible");

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> mainController.thinkingIndicator.setVisible(false));
        }


        return "Completed";
    }

    public CustomTask(MainController mainController) {
        this.mainController = mainController;
    }
}