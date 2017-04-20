package mainPackage;

import javafx.application.Platform;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class RegexUtilities {
    public static void searchAndRefresh(MainController mainController) {
        mainController.runInBackgroundThread(() -> {

            String fileToSearch = mainController.mainTextField.getText();
            String directory = mainController.directoryToSearchTextField.getText();
            mainController.files.clear();
            mainController.numberResultsLabel.setVisible(false);

            findFilesWithRegex(mainController, fileToSearch, directory);

            Platform.runLater(() -> {
                mainController.numberResultsLabel.setVisible(true);
                mainController.numberResultsLabel.setText(mainController.files.size() + " results found");
                mainController.mainTableView.refresh();
            });
        });
    }

    public static void findFilesWithRegex(MainController mainController, String fileToSearch, String directory) {

        try {

            CommonUtilities.TOTAL_FILE_COUNTER.set(0);
            CommonUtilities.MATCHING_FILE_COUNTER.set(0);

            Files.walk(Paths.get(directory)).forEach(file -> {

                CommonUtilities.TOTAL_FILE_COUNTER.incrementAndGet();

                String fileName;

                if (mainController.pathMatchingCheckbox.isSelected()) {
                    fileName = file.toAbsolutePath().toString();
                } else {
                    fileName = file.toAbsolutePath().getFileName().toString();
                }

                if (!fileToSearch.equals("")) {
                    Pattern pattern;

                    StringTokenizer st = new StringTokenizer(fileToSearch);

                    StringBuilder sb = new StringBuilder();

                    while (st.hasMoreTokens()) {

                        String next = Pattern.quote(st.nextToken());

                        sb.append(".*").append(next);
                    }

                    if (MainController.searchingTask.getFuture().isCancelled()) {

                        throw new RuntimeException();
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
