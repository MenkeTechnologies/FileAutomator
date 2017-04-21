package windows;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mainPackage.CommonUtilities;
import mainPackage.FileInfo;
import mainPackage.Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by jacobmenke on 4/15/17.
 */
public class ServiceWindow extends Stage {
    private Model model;
    private View view;
    Integer numberOfFiles;
    BorderPane root;
    TableView mainTableView;
    String destinationPathString;
    Integer startingFileNumber = 1;
    static BooleanProperty paused = new SimpleBooleanProperty(false);

    public ServiceWindow(TableView mainTableView, String destinationPathString) {
        this.mainTableView = mainTableView;
        this.destinationPathString = destinationPathString;
        this.numberOfFiles = mainTableView.getItems().size();
        Model model = new Model();
        this.model = model;
        View view = new View();
        this.view = view;
        hookupEvents();
        setScene(new Scene(root, 550, 330));
        Service myservice = (Service) model.worker;
        myservice.start();

        myservice.setOnSucceeded(event -> {

            if (model.worker.progressProperty().isEqualTo(1).get()) {
                close();
            }
        });
    }

    private void hookupEvents() {
        view.startButton.setOnAction(e -> {
            startingFileNumber = 1;
            paused.set(false);
            ((Service) model.worker).restart();
        });

        view.cancelButton.setOnAction(e -> {
            model.worker.cancel();
        });

        view.pauseButton.setOnAction(e -> {
            if (paused.get() == false) {
                paused.setValue(true);
            } else {
                paused.setValue(false);
                ((Service) model.worker).restart();
            }
        });
    }

    private class Model {
        public Worker<String> worker;
        public IntegerProperty numberOfItems = new SimpleIntegerProperty(numberOfFiles);

        public Model() {
            worker = new Service<String>() {
                @Override
                protected Task<String> createTask() {
                    return new Task<String>() {
                        @Override
                        protected String call() throws Exception {
                            updateTitle("Processing " + numberOfFiles + " files.");
                            updateMessage("Starting...");
                            updateProgress(0, numberOfFiles);
                            for (int i = startingFileNumber; i <= numberOfFiles; i++) {
                                if (isCancelled()) {
                                    updateValue("Canceled.");
                                }

                                try {

                                    if (paused.get()) {
                                        startingFileNumber = i;
                                        break;
                                    } else {
                                        copyFiles(this, i);
                                    }

                                    updateProgress(i, numberOfFiles);
                                } catch (Exception e) {
                                    if (isCancelled()) {
                                        updateValue("Canceled.");
                                    }
                                    e.printStackTrace();
                                }
                            }

                            return "Completed.";
                        }

                        private void copyFiles(Task<String> task, int i) {

                            FileInfo sourceFile = (FileInfo) mainTableView.getItems().get(i - 1);
                            updateTitle("Processing " + sourceFile.getFileName());

                            FileInfo destinationPath = new FileInfo(destinationPathString);
                            if (!destinationPath.exists()) {



                                updateMessage("Creating Directories.");



                                if (destinationPath.mkdir()) {
                                    Utilities.copyFile(sourceFile, destinationPath);
                                } else {

                                    Platform.runLater(()->{
                                        CommonUtilities.showErrorAlert("Could not create directory.");
                                    });
                                }
                            } else {
                                updateMessage("Copying Files " + i + " of " + numberOfFiles + " files.");
//
//                                try {
//                                    Files.walk(Paths.get(destinationPathString)).forEach(file->{
//
//                                        if (destinationPath.equals())
//
//                                        destinationPath.setFileName(destinationPath.getFileName()+"-"+counter);
//
//                                    });
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }

                                Utilities.copyFile(sourceFile, destinationPath);



                            }
                            updateMessage("Processed " + i + " of " + numberOfFiles + " files.");
                        }
                    };
                }
            };
        }
    }

    private class View {
        public ProgressBar progressBar;
        public Label title;
        public Label message;
        public Label running;
        public Label state;
        public Label totalWork;
        public Label workDone;
        public Label progress;
        public Label value;
        public Button startButton;
        public Button cancelButton;
        public Button pauseButton;

        public View() {
            progressBar = new ProgressBar();
            progressBar.setMinWidth(250);
            title = new Label();
            message = new Label();
            running = new Label();
            state = new Label();
            totalWork = new Label();
            workDone = new Label();
            progress = new Label();
            value = new Label();
            pauseButton = new Button("Pause");

            startButton = new Button("Restart");
            cancelButton = new Button("Cancel");

            final ReadOnlyObjectProperty<Worker.State> stateProperty = model.worker.stateProperty();
            progressBar.progressProperty().bind(model.worker.progressProperty());

            title.textProperty().bind(model.worker.titleProperty());
            message.textProperty().bind(model.worker.messageProperty());
            running.textProperty().bind(model.worker.runningProperty().asString());
            state.textProperty().bind(model.worker.stateProperty().asString());
            totalWork.textProperty().bind(model.worker.totalWorkProperty().asString());
            workDone.textProperty().bind(model.worker.workDoneProperty().asString());
            progress.textProperty().bind(Bindings.format("%5.2f%%", model.worker.progressProperty().multiply(100)));
            value.textProperty().bind(model.worker.valueProperty());

            startButton.disableProperty().bind(stateProperty.isEqualTo(Worker.State.RUNNING));
            cancelButton.disableProperty().bind(stateProperty.isNotEqualTo(Worker.State.RUNNING));
            pauseButton.disableProperty().bind(model.worker.progressProperty().isEqualTo(1));
            pauseButton.textProperty().bind(Bindings.when(paused).then("Resume").otherwise("Pause"));

            HBox topPane = new HBox(10, progressBar);
            topPane.setPadding(new Insets(10, 10, 10, 10));
            topPane.setAlignment(Pos.CENTER);
            ColumnConstraints constraints1 = new ColumnConstraints();
            constraints1.setHalignment(HPos.RIGHT);
            constraints1.setMinWidth(65);

            ColumnConstraints constraints2 = new ColumnConstraints();
            constraints2.setHalignment(HPos.LEFT);
            constraints2.setMinWidth(200);

            GridPane centerPane = new GridPane();
            centerPane.setHgap(10);
            centerPane.setVgap(10);
            centerPane.setPadding(new Insets(10, 10, 10, 10));
            centerPane.getColumnConstraints().addAll(constraints1, constraints2);
            centerPane.add(new Label("Title:"), 0, 0);
            centerPane.add(new Label("Message:"), 0, 1);
            centerPane.add(new Label("Running:"), 0, 2);
            centerPane.add(new Label("State:"), 0, 3);
            centerPane.add(new Label("Total Work:"), 0, 4);
            centerPane.add(new Label("Work Done:"), 0, 5);
            centerPane.add(new Label("Progress:"), 0, 6);
            centerPane.add(new Label("Value:"), 0, 7);

            centerPane.add(title, 1, 0);
            centerPane.add(message, 1, 1);
            centerPane.add(running, 1, 2);
            centerPane.add(state, 1, 3);
            centerPane.add(totalWork, 1, 4);
            centerPane.add(workDone, 1, 5);
            centerPane.add(progress, 1, 6);
            centerPane.add(value, 1, 7);

            HBox buttonPane = new HBox(10,
                    startButton, cancelButton, pauseButton);
            buttonPane.setPadding(new Insets(10, 10, 10, 10));
            buttonPane.setAlignment(Pos.CENTER);

            BorderPane bp = new BorderPane(centerPane, topPane, null, buttonPane, null);
            root = bp;

        }
    }
}
