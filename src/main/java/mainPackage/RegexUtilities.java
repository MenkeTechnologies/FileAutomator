package mainPackage;

import javafx.application.Platform;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class RegexUtilities {
    public static void searchAndRefresh(MainController mainController) {
        mainController.runInBackgroundThread(() -> {

            String fileToSearch = mainController.mainTextField.getText();
            String directory = mainController.directoryToSearchTextField.getText();
            mainController.files.clear();

            findFilesWithRegex(mainController, fileToSearch, directory);

            mainController.mainTableView.refresh();
        });
    }

    public static void findFilesWithRegex(MainController mainController, String fileToSearch, String directory) {


        try {

            Files.walk(Paths.get(directory)).forEach(file -> {
                CommonUtilities.FILE_COUNTER.incrementAndGet();

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
                        String next = st.nextToken();

                        sb.append(".*").append(next);
                    }

                    if (mainController.backgroundTask.getFuture().isCancelled()){
                        throw new RuntimeException();
                    }

                    String regexString = sb.toString();

                    if (mainController.caseInsensitiveMatchingCheckbox.isSelected()) {
                        pattern = Pattern.compile(regexString, Pattern.CASE_INSENSITIVE);
                    } else {
                        pattern = Pattern.compile(regexString);
                    }

                    if (pattern.matcher(fileName).find()) {

                        mainController.backgroundTask.updateMessage("Found file number " + CommonUtilities.FILE_COUNTER + " : " + file.getFileName());

                        mainController.checkToShowHiddenFiles(file);
                    }
                } else {

                    mainController.checkToShowHiddenFiles(file);
                }
            });
        } catch (Exception e) {

            System.out.println("Stopped indexing.");
        }
    }
}
