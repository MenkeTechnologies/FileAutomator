package mainPackage;

import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * Created by jacobmenke on 4/16/17.
 */
public class RegexUtilities {
    static class CustomFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.SKIP_SUBTREE;
        }
    }

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
                if (child instanceof TextField) {

                    TextField tf = (TextField) child;
                    orTerms.add(tf.getText());
                } else if (child instanceof HBox) {
                    Label label = (Label) ((HBox) child).getChildren().get(0);
                    TextField tf = (TextField) ((HBox) child).getChildren().get(1);

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

            // Pre-compile all patterns ONCE before the file walk
            boolean hasFilter = !orTerms.get(0).equals("");
            ArrayList<Pattern> compiledPatterns = new ArrayList<>();

            if (hasFilter) {
                boolean caseInsensitive = mainController.caseInsensitiveMatchingCheckbox.isSelected();

                for (String fileString : orTerms) {
                    StringBuilder andTermsBuilder = new StringBuilder(fileString);
                    for (String term : andTerms) {
                        andTermsBuilder.append(" ").append(term).append(" ");
                    }

                    StringTokenizer st = new StringTokenizer(andTermsBuilder.toString());
                    StringBuilder sb = new StringBuilder();
                    while (st.hasMoreTokens()) {
                        sb.append(".*").append(Pattern.quote(st.nextToken()));
                    }

                    compiledPatterns.add(caseInsensitive
                            ? Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE)
                            : Pattern.compile(sb.toString()));
                }
            }

            boolean matchPath = mainController.pathMatchingCheckbox.isSelected();

            Files.walkFileTree(Paths.get(directory), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.SKIP_SUBTREE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                    if (MainController.searchingTask.getFuture().isCancelled()) {
                        throw new RuntimeException();
                    }

                    long total = CommonUtilities.TOTAL_FILE_COUNTER.incrementAndGet();

                    if (hasFilter) {
                        String fileName = matchPath
                                ? file.toAbsolutePath().toString()
                                : file.toAbsolutePath().getFileName().toString();

                        for (Pattern pattern : compiledPatterns) {
                            if (pattern.matcher(fileName).find()) {
                                CommonUtilities.MATCHING_FILE_COUNTER.incrementAndGet();
                                mainController.checkToShowHiddenFiles(file);
                                break;
                            }
                        }
                    } else {
                        mainController.checkToShowHiddenFiles(file);
                    }

                    // Throttle UI updates to every 100 files
                    if (total % 100 == 0) {
                        MainController.searchingTask.updateMessage(
                                "Filtered " + CommonUtilities.MATCHING_FILE_COUNTER +
                                " files of " + total + " files : Processing " + file.getFileName());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException fse) {
            System.err.println("Swallowing IOException in file search: " + fse.getMessage());
        } catch (RuntimeException e) {
            System.err.println("File search cancelled.");
        }
    }
}
