package fr.socolin.awesomeLogViewer.core.core.tool_window.log_list.renderer

import com.intellij.openapi.editor.ex.util.EditorUtil
import java.awt.font.FontRenderContext
import java.awt.geom.AffineTransform
import kotlin.math.ceil

class CellFontHelper {
    companion object {
        val cachedValue = mutableMapOf<String, Int>()
        fun getIdealColumnSize(sample: String): Int {

            val editorFont = EditorUtil.getEditorFont()
            val key = sample + editorFont.size;
            return cachedValue.getOrPut(key) {
                ceil(editorFont.getStringBounds(sample, FontRenderContext(AffineTransform(), true, true)).maxX).toInt() + 8
            }
        }
    }
}
