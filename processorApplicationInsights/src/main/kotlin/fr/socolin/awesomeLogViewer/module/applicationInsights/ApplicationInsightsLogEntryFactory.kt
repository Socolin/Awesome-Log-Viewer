package fr.socolin.awesomeLogViewer.module.applicationInsights

import com.google.gson.Gson
import com.google.gson.JsonParser
import fr.socolin.awesomeLogViewer.core.core.session.LogEntryTimeInfo
import fr.socolin.awesomeLogViewer.module.applicationInsights.data.*
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeFormatter

class ApplicationInsightsLogEntryFactory {
    companion object {
        val gson = Gson()

        fun createFromJson(
            json: String,
            sampled: Boolean,
            configured: Boolean
        ): ApplicationInsightsLogEntry? {
            val jsonObject = JsonParser.parseString(json).asJsonObject
            val nameProperty = jsonObject.get("name")
            if (!nameProperty.isJsonPrimitive) {
                return null
            }
            val name = nameProperty.asString

            val dataProperty = jsonObject.get("data")
            if (!dataProperty.isJsonObject) {
                return null
            }

            val baseData = dataProperty.asJsonObject.get("baseData")
            if (!baseData.isJsonObject) {
                return null
            }

            val telemetryType = ApplicationInsightsTelemetryType.Companion.fromType(name)
            val telemetryData: ApplicationInsightsTelemetryData? = when (telemetryType) {
                ApplicationInsightsTelemetryType.Message -> gson.fromJson(
                    baseData,
                    ApplicationInsightsMessageTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.Request -> gson.fromJson(
                    baseData,
                    ApplicationInsightsRequestTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.Exception -> gson.fromJson(
                    baseData,
                    ApplicationInsightsExceptionTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.PageView -> gson.fromJson(
                    baseData,
                    ApplicationInsightsPageViewTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.Metric -> gson.fromJson(
                    baseData,
                    ApplicationInsightsMetricTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.Event -> gson.fromJson(
                    baseData,
                    ApplicationInsightsEventTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.RemoteDependency -> gson.fromJson(
                    baseData,
                    ApplicationInsightsRemoteDependencyTelemetryData::class.java
                )

                ApplicationInsightsTelemetryType.Unk -> null
            }

            if (telemetryData == null) {
                return null
            }

            val tags: Map<String, String> = jsonElementToMap(jsonObject.get("tags"))

            val parentId = tags["ai.operation.parentId"]
            val id = telemetryData.id

            val ta = DateTimeFormatter.ISO_INSTANT.parse(jsonObject["time"].asString)
            val timestamp = Instant.from(ta)

            // FIXME: Include sample rate
            return ApplicationInsightsLogEntry(
                telemetryType,
                telemetryData,
                configured,
                sampled,
                json,
                tags,
                telemetryData.getSeverityLevel() ?: ApplicationInsightsLogSeverityLevel.INFO,
                LogEntryTimeInfo.createFromStartAndDuration(timestamp, telemetryData.getDuration() ?: Duration.ZERO),
                id,
                parentId
            )
        }

        fun jsonElementToMap(jsonElement: com.google.gson.JsonElement): Map<String, String> {
            val map = mutableMapOf<String, String>();

            if (jsonElement.isJsonObject) {
                jsonElement.asJsonObject.entrySet().forEach {
                    map[it.key] = it.value.asString;
                }
            }
            return map;
        }
    }
}
