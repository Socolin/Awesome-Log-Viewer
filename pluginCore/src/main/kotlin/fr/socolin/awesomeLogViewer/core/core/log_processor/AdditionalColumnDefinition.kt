package fr.socolin.awesomeLogViewer.core.core.log_processor

data class AdditionalColumnDefinition(
    val propertyName: String,
    val displayName: String,
) {
    val columnId: String
        get() = COLUMN_ID_PREFIX + propertyName

    companion object {
        const val COLUMN_ID_PREFIX = "property:"

        fun propertyNameFromColumnId(columnId: String?): String? {
            if (columnId == null || !columnId.startsWith(COLUMN_ID_PREFIX))
                return null
            return columnId.substring(COLUMN_ID_PREFIX.length)
        }
    }
}
