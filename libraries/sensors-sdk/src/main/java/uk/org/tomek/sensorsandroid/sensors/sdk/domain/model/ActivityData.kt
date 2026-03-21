package uk.org.tomek.sensorsandroid.sensors.sdk.domain.model

data class ActivityData(
    val timestamp: Long,
    val type: ActivityType,
    val confidence: Int // 0-100
)

enum class ActivityType {
    STILL,
    WALKING,
    RUNNING,
    ON_BICYCLE,
    IN_VEHICLE,
    UNKNOWN
}
