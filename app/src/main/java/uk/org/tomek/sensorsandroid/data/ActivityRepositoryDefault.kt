package uk.org.tomek.sensorsandroid.data

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.domain.ActivityRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityData

class ActivityRepositoryDefault(
    private val sensorsSdk: SensorsSdk
) : ActivityRepository {
    override val activityDataFlow: Flow<ActivityData> = sensorsSdk.activityDataFlow

    override fun startActivityRecognition(): Result<Unit> {
        return sensorsSdk.startActivityRecognition()
    }

    override fun stopActivityRecognition() {
        sensorsSdk.stopActivityRecognition()
    }
}
