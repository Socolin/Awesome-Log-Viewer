package fr.socolin.awesomeLogViewer.core.core.session.debug

import com.intellij.openapi.application.ApplicationInfo

class DebugOutputHelper {
    companion object {
        fun isDebugOutputSupported(): Boolean {
            val productCode = ApplicationInfo.getInstance().build.productCode

            return when (productCode) {
                "RD" -> true
                else -> false
            }
        }
    }
}
