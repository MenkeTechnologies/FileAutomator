package sample;

import com.apple.eio.FileManager;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import org.apache.commons.io.FileUtils;

import javax.naming.Binding;
import java.awt.*;
import java.awt.Button;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * Created by jacobmenke on 4/13/17.
 */
public class Utilities {
    public static Integer index = 0;

    public static String quote(String s) {

        StringBuilder sb = new StringBuilder();

        sb.append("\"").append(s).append("\"");
        return sb.toString();
    }

    public static ContextMenu createContextMenu(FileInfo fileInfo, TableView mainTableView, ObservableList<FileInfo> files, MainController mainController) {
        ContextMenu rowContextMenu = new ContextMenu();
        String name = "Open \"" + fileInfo.getFileName() + "\"";
        String parentDirectory = fileInfo.getParent();
        System.out.println(name);

        MenuItem openItem = new MenuItem(name);
        MenuItem openInEnclosingItem = new MenuItem("Open Parent Directory \"" + parentDirectory + "\"");
        MenuItem deleteItem = new MenuItem("Move To Trash ");
        MenuItem secureDeleteItem = new Menu("Secure Delete");
        MenuItem renameItem = new MenuItem("Rename");
        MenuItem copyItem = new MenuItem("Copy");
        rowContextMenu.getItems().addAll(openItem, openInEnclosingItem, deleteItem, secureDeleteItem, renameItem, copyItem);

        copyItem.setOnAction(e -> {

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
                        mainController.searchAndRefresh();
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
        });

        secureDeleteItem.setOnAction(e -> {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

            alert.setHeaderText("Are you sure you want to delete this item permanently?");

            Optional<ButtonType> res = alert.showAndWait();

            if (res.get() == ButtonType.OK) {
                try {
                    invokeCommandLine("rm", "-rf", fileInfo.getAbsolutePath());
                    if (fileInfo.isDirectory()) {
                        mainController.searchAndRefresh();
                    } else {
                        files.remove(fileInfo);
                        mainTableView.refresh();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        deleteItem.setOnAction(e -> {
            try {
                FileManager.moveToTrash(fileInfo);
                if (fileInfo.isDirectory()) {

                    mainController.searchAndRefresh();
                } else {
                    files.remove(fileInfo);
                    mainTableView.refresh();
                }
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
        });

        openItem.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(fileInfo);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        openInEnclosingItem.setOnAction(e -> {

            try {
                Desktop.getDesktop().open(fileInfo.getParentFile());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        renameItem.setOnAction(e -> {
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


                if (oldFile.isDirectory()){
                    System.out.println("here in change dir");
                    mainController.searchAndRefresh();
                } else {
                    System.out.println("not dir");
                    FileInfo newFIle = new FileInfo(newFilePath);

                    files.add(index, newFIle);

                    mainTableView.getSelectionModel().select(files.get(index));

                    mainTableView.refresh();
                }
            }
        });

        return rowContextMenu;
    }

    private static void invokeCommandLine(String... command) throws IOException {

        ProcessBuilder pb = new ProcessBuilder();
        pb.command(command);

        pb.start();
    }
}
