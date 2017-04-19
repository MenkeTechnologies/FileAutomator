package mainPackage;

import com.apple.eio.FileManager;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jacobmenke on 4/15/17.
 */
public class CommonUtilities {
    public static String turnBytesIntoHumanReadable(long length) {
        Integer power = 0;

        String suffix = "B";

        if (length < 1024){

        } else if (length < Math.pow(1024,2)){
            power =1;
            suffix="KiB";

        } else if (length < Math.pow(1024,3)){
            power =2;
            suffix="MiB";
        } else if (length < Math.pow(1024, 4)){
            power =3;
            suffix="GiB";
        } else if (length < Math.pow(1024,5)){
            power =4;
            suffix="TiB";
        }

        Double factor = Math.pow(1024,power);

        return String.format("%.2f %s",length / factor, suffix);



    }

    public static String formatDuration(Duration duration){
        double millis = duration.toMillis();
        int seconds = (int) (millis/1000) % 60;
        int minutes = (int) (millis/(1000 * 60));
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static Integer index = 0;
    public static AtomicInteger FILE_COUNTER = new AtomicInteger(0);

    public static void createNewFile(FileInfo fileInfo, MainController mainController) {
        TextInputDialog textInputDialog = new TextInputDialog("");
        textInputDialog.setHeaderText("Create New File");

        Optional<String> result = textInputDialog.showAndWait();
        if (result.isPresent()) {
            try {
                String path = fileInfo + File.separator + result.get();
                File newFile = new File(path);
                System.out.println(path);
                newFile.createNewFile();
                RegexUtilities.searchAndRefresh(mainController);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void openFile(FileInfo fileInfo) {
        try {
            Desktop.getDesktop().open(fileInfo);
        } catch (IOException e1) {
            showErrorAlert("The file could not be opened.");
            e1.printStackTrace();
        }
    }
    public static void openEnclosingDirectory(FileInfo fileInfo) {
        try {
            Desktop.getDesktop().open(fileInfo.getParentFile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void secureDelete(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Are you sure you want to delete \"" + fileInfo.getFileName() + "\" permanently?");

        Optional<ButtonType> res = alert.showAndWait();

        if (res.get() == ButtonType.OK) {
            try {
                invokeCommandLine("rm", "-rf", fileInfo.getAbsolutePath());
                if (fileInfo.isDirectory()) {
                    RegexUtilities.searchAndRefresh(mainController);
                } else {
                    files.remove(fileInfo);
                    mainTableView.refresh();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private static void invokeCommandLine(String... command) throws IOException {

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command);

        pb.start();
    }

    public static String quote(String s) {

        StringBuilder sb = new StringBuilder();

        sb.append("\"").append(s).append("\"");
        return sb.toString();
    }

    public static void copyItem(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {
        TextInputDialog textInputDialog = new TextInputDialog(fileInfo.getAbsolutePath());

        textInputDialog.getDialogPane().setPrefWidth(600);
//            textInputDialog.setHeaderText("Copy " + quote(fileInfo.getFileName()) + " to " + textInputDialog.contentTextProperty());

        textInputDialog.setContentText("New File: ");

        textInputDialog.headerTextProperty().bind(Bindings.concat("Copy ", quote(fileInfo.getAbsolutePath()), " to \"", textInputDialog.getEditor().textProperty(), "\""));

        Optional<String> result = textInputDialog.showAndWait();

        if (result.isPresent()) {
            try {
                String newFileName = result.get();
//                    String newFileToCreate = fileInfo.getParentFile().toString() + File.separator + newFileName;

                FileInfo newFileInfo = new FileInfo(newFileName);

                if (fileInfo.isDirectory()) {
                    FileUtils.copyDirectory(fileInfo, new File(newFileName));
                    RegexUtilities.searchAndRefresh(mainController);
                } else {
                    FileUtils.copyFile(fileInfo, new File(newFileName));
                    index = files.indexOf(fileInfo);
                    files.add(++index, new FileInfo(newFileName));
                    mainTableView.refresh();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void moveToTrash(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setHeaderText("Are you sure you want to delete \"" + fileInfo.getFileName() + "\"?");

        Optional<ButtonType> res = alert.showAndWait();

        if (res.get() == ButtonType.OK) {

            try {

                FileManager.moveToTrash(fileInfo);
                if (fileInfo.isDirectory()) {

                    RegexUtilities.searchAndRefresh(mainController);
                } else {
                    files.remove(fileInfo);
                    mainTableView.refresh();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void renameFile(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {
        TextInputDialog dialog = new TextInputDialog(fileInfo.getFileName());
        dialog.setHeaderText("Rename " + "\"" + fileInfo.getFileName() + "\"");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {

            String path = fileInfo.getParent();
            File oldFile = fileInfo;
            String fileName = result.get();

            String newFilePath = path + File.separator + fileName;
            Integer index = files.indexOf(fileInfo);

            files.remove(fileInfo);

            fileInfo.renameTo(new File(newFilePath));

            if (oldFile.isDirectory()) {
                System.out.println("here in change dir");
                RegexUtilities.searchAndRefresh(mainController);
            } else {
                System.out.println("not dir");
                FileInfo newFIle = new FileInfo(newFilePath);

                files.add(index, newFIle);

                mainTableView.getSelectionModel().select(files.get(index));

                mainTableView.refresh();
            }
        }
    }

    public static void showErrorAlert(String error) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(error);
        alert.showAndWait();
    }

    public static void copyToClipboard(String outputText) {
        StringSelection stringSelection = new StringSelection(outputText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);
    }
}
