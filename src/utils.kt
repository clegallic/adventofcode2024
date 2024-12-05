import java.io.File

fun readInput(day: Int, fileName: String, removeBlankLines: Boolean = true) =
    "inputs/d${day.toString().padStart(2, '0')}/${fileName}.txt"
        .let { filePath ->
            File(filePath)
                .readText()
                .lines()
                .filter { !removeBlankLines || it.isNotBlank()}
        }

fun String.splitWhiteSpaces() = this.split("\\s+".toRegex())
