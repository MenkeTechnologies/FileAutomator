package mainPackage;

import javafx.application.Platform;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class RegexUtilities {
    public static void searchAndRefresh(MainController mainController) {
        mainController.mainTableView.getItems().clear();
        mainController.mainTableView.refresh();

        mainController.runInBackgroundThread(() -> {

            String fileToSearch = mainController.mainTextField.getText();
            String directory = mainController.directoryToSearchTextField.getText();
            mainController.files.clear();
            mainController.numberResultsLabel.setVisible(false);

            ArrayList<String> orTerms = new ArrayList<>();
            ArrayList<String> andTerms = new ArrayList<>();

            mainController.filterHBox.getChildren().forEach(child -> {
                if (child instanceof TextField){

                    TextField tf = (TextField)child;
                    orTerms.add(tf.getText());

                } else if (child instanceof HBox){
                    Label label = (Label)((HBox) child).getChildren().get(0);
                    TextField tf = (TextField)((HBox)child).getChildren().get(1);

                    if (!tf.getText().equals("")) {
                        if (label.getText().equals("OR")) {
                            orTerms.add(tf.getText());
                        } else {
                            andTerms.add(tf.getText());
                        }
                    }
                }

            });


            findFilesWithRegex(mainController, directory, andTerms, orTerms);

            Platform.runLater(() -> {
                mainController.numberResultsLabel.setVisible(true);
                mainController.numberResultsLabel.setText(mainController.files.size() + " results found");
                mainController.mainTableView.refresh();
            });
        });
    }

    public static void findFilesWithRegex(MainController mainController, String directory, ArrayList<String> andTerms, ArrayList<String> orTerms) {

        try {

            CommonUtilities.TOTAL_FILE_COUNTER.set(0);
            CommonUtilities.MATCHING_FILE_COUNTER.set(0);

            Files.walk(Paths.get(directory)).forEach(file -> {

                if (MainController.searchingTask.getFuture().isCancelled()) {

                    throw new RuntimeException();
                }


                CommonUtilities.TOTAL_FILE_COUNTER.incrementAndGet();

                String fileName;

                if (mainController.pathMatchingCheckbox.isSelected()) {
                    fileName = file.toAbsolutePath().toString();
                } else {
                    fileName = file.toAbsolutePath().getFileName().toString();
                }

                if (!orTerms.get(0).equals("")) {

                    orTerms.forEach(fileString->{

                        Pattern pattern;

                        StringBuilder andTermsBuilder = new StringBuilder(fileString);

                        andTerms.forEach(term->{
                            andTermsBuilder.append(" ").append(term).append(" ");
                        });

                        StringTokenizer st = new StringTokenizer(andTermsBuilder.toString());

//                        System.out.println("compound searcher = " + andTermsBuilder.toString());

                        StringBuilder sb = new StringBuilder();

                        while (st.hasMoreTokens()) {

                            String next = Pattern.quote(st.nextToken());

                            sb.append(".*").append(next);
                        }


                        String regexString = sb.toString();

                        if (mainController.caseInsensitiveMatchingCheckbox.isSelected()) {
                            pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
                        } else {
                            pattern = Pattern.compile(regexString);
                        }

                        if (pattern.matcher(fileName).find()) {

                            CommonUtilities.MATCHING_FILE_COUNTER.incrementAndGet();

                            mainController.checkToShowHiddenFiles(file);
                        }

                    });

                } else {

                    mainController.checkToShowHiddenFiles(file);
                }

                String message = "Filtered " + CommonUtilities.MATCHING_FILE_COUNTER + " files of " + CommonUtilities.TOTAL_FILE_COUNTER + " files : Processing " + file.getFileName();

                MainController.searchingTask.updateMessage(message);
            });
        } catch (Exception e) {
            e.printStackTrace();

            System.out.println("Stopped indexing.");
        }
    }
}
