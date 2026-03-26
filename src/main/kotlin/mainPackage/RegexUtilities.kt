package mainPackage

import javafx.application.Platform
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.StringTokenizer
import java.util.regex.Pattern

object RegexUtilities {
    @JvmStatic
    fun searchAndRefresh(mainController: MainController) {
        mainController.mainTableView.items.clear()
        mainController.mainTableView.refresh()

        mainController.runInBackgroundThread {
            val orTerms = ArrayList<String>()
            val andTerms = ArrayList<String>()

            mainController.filterHBox.children.forEach { child ->
                when (child) {
                    is TextField -> orTerms.add(child.text)
                    is HBox -> {
                        val label = child.children[0] as Label
                        val tf = child.children[1] as TextField
                        if (tf.text != "") {
                            if (label.text == "OR") orTerms.add(tf.text)
                            else andTerms.add(tf.text)
                        }
                    }
                    else -> {}
                }
            }

            val directory = mainController.directoryToSearchTextField.text
            mainController.files.clear()
            mainController.numberResultsLabel.isVisible = false

            findFilesWithRegex(mainController, directory, andTerms, orTerms)

            Platform.runLater {
                mainController.numberResultsLabel.isVisible = true
                mainController.numberResultsLabel.text = "${mainController.files.size} results found"
                mainController.mainTableView.refresh()
            }
        }
    }

    @JvmStatic
    fun findFilesWithRegex(mainController: MainController, directory: String, andTerms: ArrayList<String>, orTerms: ArrayList<String>) {
        try {
            CommonUtilities.TOTAL_FILE_COUNTER.set(0)
            CommonUtilities.MATCHING_FILE_COUNTER.set(0)

            val hasFilter = orTerms[0] != ""
            val compiledPatterns = ArrayList<Pattern>()

            if (hasFilter) {
                val caseInsensitive = mainController.caseInsensitiveMatchingCheckbox.isSelected
                for (fileString in orTerms) {
                    val andTermsBuilder = StringBuilder(fileString)
                    for (term in andTerms) {
                        andTermsBuilder.append(" ").append(term).append(" ")
                    }
                    val st = StringTokenizer(andTermsBuilder.toString())
                    val sb = StringBuilder()
                    while (st.hasMoreTokens()) {
                        sb.append(".*").append(Pattern.quote(st.nextToken()))
                    }
                    compiledPatterns.add(
                        if (caseInsensitive) Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE)
                        else Pattern.compile(sb.toString())
                    )
                }
            }

            val matchPath = mainController.pathMatchingCheckbox.isSelected

            Files.walkFileTree(Paths.get(directory), object : SimpleFileVisitor<Path>() {
                override fun visitFileFailed(file: Path, exc: IOException): FileVisitResult = FileVisitResult.SKIP_SUBTREE

                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (MainController.searchingTask.future?.isCancelled == true) {
                        throw RuntimeException()
                    }

                    val total = CommonUtilities.TOTAL_FILE_COUNTER.incrementAndGet()

                    if (hasFilter) {
                        val fileName = if (matchPath) file.toAbsolutePath().toString()
                            else file.toAbsolutePath().fileName.toString()

                        for (pattern in compiledPatterns) {
                            if (pattern.matcher(fileName).find()) {
                                CommonUtilities.MATCHING_FILE_COUNTER.incrementAndGet()
                                mainController.checkToShowHiddenFiles(file)
                                break
                            }
                        }
                    } else {
                        mainController.checkToShowHiddenFiles(file)
                    }

                    if (total % 500 == 0L) {
                        MainController.searchingTask.updateMessage(
                            "Filtered ${CommonUtilities.MATCHING_FILE_COUNTER.get()} files of $total files : Processing ${file.fileName}"
                        )
                    }

                    return FileVisitResult.CONTINUE
                }
            })
        } catch (fse: IOException) {
            System.err.println("Swallowing IOException in file search: ${fse.message}")
        } catch (e: RuntimeException) {
            System.err.println("File search cancelled.")
        }
    }
}
