package mainPackage

object FileTypeUtilities {
    @JvmStatic
    fun determineIfTextual(type: String): Boolean = when (type) {
        "pdf", "java", "py", "json", "rb", "js", "fxml", "xml", "html", "css",
        "doc", "docx", "xls", "xlsx", "txt", "jar", "pl", "tcl", "c", "cpp",
        "h", "swift", "plist", "conf" -> true
        else -> false
    }

    private val EXTENSION_MAP = HashMap<String, String>(32).apply {
        for (ext in arrayOf("png", "jpg", "jpeg", "svg")) put(ext, "image")
        put("psd", "psd"); put("ai", "ai")
        for (ext in arrayOf("mp4", "mov")) put(ext, "video")
        put("java", "java"); put("py", "python"); put("rb", "ruby")
        for (ext in arrayOf("json", "js")) put(ext, "js")
        for (ext in arrayOf("fxml", "xml")) put(ext, "xml")
        put("pdf", "pdf"); put("html", "html"); put("css", "css")
        for (ext in arrayOf("mp3", "wav", "aiff", "flac")) put(ext, "music")
        for (ext in arrayOf("doc", "docx")) put(ext, "word")
        for (ext in arrayOf("xls", "xlsx")) put(ext, "excel")
        put("txt", "text"); put("jar", "jar")
    }

    @JvmStatic
    fun getFileType(pathName: String): String {
        val dotIndex = pathName.lastIndexOf('.')
        if (dotIndex <= 0 || dotIndex == pathName.length - 1) return "file"
        val ext = pathName.substring(dotIndex + 1)
        return EXTENSION_MAP[ext] ?: "file"
    }
}
