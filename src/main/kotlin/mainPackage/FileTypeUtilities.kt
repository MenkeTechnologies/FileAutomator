package mainPackage

object FileTypeUtilities {
    @JvmStatic
    fun determineIfTextual(type: String): Boolean = when (type) {
        "pdf", "java", "py", "json", "rb", "js", "fxml", "xml", "html", "css",
        "doc", "docx", "xls", "xlsx", "txt", "jar", "pl", "tcl", "c", "cpp",
        "h", "swift", "plist", "conf" -> true
        else -> false
    }

    @JvmStatic
    fun getFileType(pathName: String): String {
        if (pathName.lastIndexOf('.') <= 0) return "file"
        val fileType = pathName.substring(pathName.lastIndexOf('.') + 1)
        return when (fileType) {
            "png", "jpg", "jpeg", "svg" -> "image"
            "psd" -> "psd"
            "ai" -> "ai"
            "mp4", "mov" -> "video"
            "java" -> "java"
            "py" -> "python"
            "rb" -> "ruby"
            "json", "js" -> "js"
            "fxml", "xml" -> "xml"
            "pdf" -> "pdf"
            "html" -> "html"
            "css" -> "css"
            "mp3", "wav", "aiff", "flac" -> "music"
            "doc", "docx" -> "word"
            "xls", "xlsx" -> "excel"
            "txt" -> "text"
            "jar" -> "jar"
            else -> "file"
        }
    }
}
