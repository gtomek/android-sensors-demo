package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityData

interface ActivityRepository {
    val activityDataFlow: Flow<ActivityData>

    fun startActivityRecognition(): Result<Unit>
    fun stopActivityRecognition()
}
