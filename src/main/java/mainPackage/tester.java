package mainPackage;

import javafx.scene.control.CheckBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class tester {
    public static void main(String[] args) throws IOException {

        modifyStylesheet("*", "-fx-base", true, "ping");
    }

    public static void modifyStylesheet(String element, String prop, boolean append, String value) {


    }
}