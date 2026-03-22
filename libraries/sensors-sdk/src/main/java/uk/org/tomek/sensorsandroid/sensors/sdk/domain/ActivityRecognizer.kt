package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityData

internal interface ActivityRecognizer {
    val activityDataFlow: Flow<ActivityData>

    fun startListening(): Result<Unit>
    fun stopListening()
}
