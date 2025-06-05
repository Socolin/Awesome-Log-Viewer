package fr.socolin.awesomeLogViewer.module.simpleConsole

class AnsiCodeUtil {
    companion object {
        const val ESC: String = "\u001B"

        fun removeAnsiCodes(text: String): String {
            return text.replace(Regex("$ESC\\[[;\\d]*m"), "")
        }
    }
}
