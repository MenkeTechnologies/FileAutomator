package mainPackage

import javafx.scene.control.ContextMenu
import javafx.scene.control.MenuItem
import org.fxmisc.flowless.VirtualizedScrollPane
import org.fxmisc.richtext.LineNumberFactory
import org.fxmisc.richtext.model.StyleSpans
import org.fxmisc.richtext.model.StyleSpansBuilder
import java.io.FileOutputStream
import java.io.PrintWriter
import java.util.Collections
import java.util.regex.Pattern

class CodeTextArea {
    var mainController: MainController? = null
    var codeArea: org.fxmisc.richtext.CodeArea

    constructor(mainController: MainController) {
        this.mainController = mainController
        codeArea = org.fxmisc.richtext.CodeArea()
        codeArea.paragraphGraphicFactory = LineNumberFactory.get(codeArea)
        codeArea.richChanges()
            .filter { ch -> ch.inserted != ch.removed }
            .subscribe { codeArea.setStyleSpans(0, computeHighlighting(codeArea.text)) }
        setText(SAMPLE_CODE)
        val cm = ContextMenu()
        val menuItem = MenuItem("Save")
        cm.items.add(menuItem)
        codeArea.isWrapText = true
        codeArea.contextMenu = cm
        menuItem.setOnAction {
            val path = mainController.pathLabelContent.text
            try {
                PrintWriter(FileOutputStream(path)).use { pw ->
                    pw.print(codeArea.text)
                    MainController.loadingTask.updateMessage("Saved $path!")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CommonUtilities.showErrorAlert("Could not save $path")
            }
        }
    }

    constructor()  {
        codeArea = org.fxmisc.richtext.CodeArea()
    }

    fun setText(text: String) { codeArea.replaceText(0, 0, text) }
    fun getCodeArea(): VirtualizedScrollPane<org.fxmisc.richtext.CodeArea> = VirtualizedScrollPane(codeArea)

    companion object {
        private val KEYWORDS = arrayOf(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"
        )
        private val KEYWORD_PATTERN = "\\b(${KEYWORDS.joinToString("|")})\\b"
        private val PAREN_PATTERN = "\\(|\\)"
        private val BRACE_PATTERN = "\\{|\\}"
        private val BRACKET_PATTERN = "\\[|\\]"
        private val SEMICOLON_PATTERN = "\\;"
        private val STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\""
        private val COMMENT_PATTERN = "//[^\n]*|/\\*(.|\\R)*?\\*/"
        private val PATTERN = Pattern.compile(
            "(?<KEYWORD>$KEYWORD_PATTERN)|(?<PAREN>$PAREN_PATTERN)|(?<BRACE>$BRACE_PATTERN)|(?<BRACKET>$BRACKET_PATTERN)|(?<SEMICOLON>$SEMICOLON_PATTERN)|(?<STRING>$STRING_PATTERN)|(?<COMMENT>$COMMENT_PATTERN)"
        )
        private val SAMPLE_CODE = listOf(
            "package com.example;", "", "import java.util.*;", "",
            "public class Foo extends Bar implements Baz {", "",
            "    /*", "     * multi-line comment", "     */",
            "    public static void main(String[] args) {",
            "        // single-line comment",
            "        for(String arg: args) {",
            "            if(arg.length() != 0)", "                System.out.println(arg);",
            "            else", "                System.err.println(\"Warning: empty string as argument\");",
            "        }", "    }", "", "}"
        ).joinToString("\n")

        private fun computeHighlighting(text: String): StyleSpans<Collection<String>> {
            val matcher = PATTERN.matcher(text)
            var lastKwEnd = 0
            val spansBuilder = StyleSpansBuilder<Collection<String>>()
            while (matcher.find()) {
                val styleClass = when {
                    matcher.group("KEYWORD") != null -> "keyword"
                    matcher.group("PAREN") != null -> "paren"
                    matcher.group("BRACE") != null -> "brace"
                    matcher.group("BRACKET") != null -> "bracket"
                    matcher.group("SEMICOLON") != null -> "semicolon"
                    matcher.group("STRING") != null -> "string"
                    matcher.group("COMMENT") != null -> "comment"
                    else -> null
                }!!
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd)
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start())
                lastKwEnd = matcher.end()
            }
            spansBuilder.add(Collections.emptyList(), text.length - lastKwEnd)
            return spansBuilder.create()
        }
    }
}
