package mainPackage.StylesheetUtilities

import javafx.scene.Scene
import mainPackage.MainController
import java.io.File
import java.io.FileOutputStream
import java.io.PrintWriter
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Paths

class Modify {
    companion object {
        @JvmField
        var tempCssFile: String = System.getProperty("user.home") + File.separator + "modify.css"
        @JvmField
        var scenes: ArrayList<Scene?> = ArrayList()

        @JvmStatic
        fun modifyStylesheet(mainController: MainController, element: String, prop: String, append: Boolean, value: String) {
            val lines: MutableList<String> = ArrayList()

            try {
                lines.addAll(Files.readAllLines(Paths.get(tempCssFile)))

                var startingIndex = -1
                var endingIndex = 0
                var propIndex = -1

                for (i in lines.indices) {
                    if (lines[i].contains(element)) {
                        startingIndex = i
                        break
                    }
                }

                if (startingIndex == -1) {
                    startingIndex = 0
                } else {
                    for (i in startingIndex until lines.size) {
                        if (lines[i].contains("}")) {
                            endingIndex = i
                            break
                        }
                    }

                    for (i in startingIndex until endingIndex) {
                        if (lines[i].contains(prop)) {
                            propIndex = i
                            break
                        }
                    }

                    if (!append) {
                        lines.subList(startingIndex + 1, endingIndex).clear()
                    } else {
                        if (propIndex != -1) {
                            lines.subList(propIndex, propIndex + 1).clear()
                        }
                    }
                }

                val line = "\t$prop: $value;"
                lines.add(startingIndex + 1, line)
            } catch (e1: Exception) {
                e1.printStackTrace()
            }

            try {
                PrintWriter(FileOutputStream(tempCssFile)).use { pw ->
                    for (line in lines) {
                        pw.println(line)
                    }
                    addStyleSheets(scenes, tempCssFile)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        @JvmStatic
        fun addStyleSheets(scenes: ArrayList<Scene?>, tempCssFile: String) {
            for (scene in scenes) {
                if (scene != null) {
                    scene.stylesheets.clear()
                    scene.stylesheets.add("stylesheets/styles.css")
                    try {
                        val url = File(tempCssFile).toURI().toURL()
                        scene.stylesheets.add(url.toExternalForm())
                    } catch (e: MalformedURLException) {
                        e.printStackTrace()
                    }
                } else {
                    System.err.println("___________${Thread.currentThread().stackTrace[1].className}____Line:${Thread.currentThread().stackTrace[1].lineNumber}___ $scene was null.")
                }
            }
        }
    }
}
