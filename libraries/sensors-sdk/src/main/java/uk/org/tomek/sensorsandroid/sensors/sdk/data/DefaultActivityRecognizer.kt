package uk.org.tomek.sensorsandroid.sensors.sdk.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.ActivityRecognizer
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityData
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.ActivityType

class DefaultActivityRecognizer(private val context: Context) : ActivityRecognizer {

    private val activityRecognitionClient = ActivityRecognition.getClient(context)
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityRecognitionReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    override val activityDataFlow: SharedFlow<ActivityData> = _activityDataFlow.asSharedFlow()

    @SuppressLint("MissingPermission")
    override fun startListening(): Result<Unit> {
        if (!hasPermission()) {
            return Result.failure(SecurityException("Missing ACTIVITY_RECOGNITION permission"))
        }

        return try {
            activityRecognitionClient.requestActivityUpdates(10000, pendingIntent)
            instance = this
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @SuppressLint("MissingPermission")
    override fun stopListening() {
        if (hasPermission()) {
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
        }
    }

    private fun hasPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    companion object {
        private val _activityDataFlow = MutableSharedFlow<ActivityData>(
            extraBufferCapacity = 10,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        @Volatile
        private var instance: DefaultActivityRecognizer? = null

        fun handleActivityResult(result: ActivityRecognitionResult) {
            val detectedActivity = result.mostProbableActivity
            val type = when (detectedActivity.type) {
                DetectedActivity.WALKING -> ActivityType.WALKING
                DetectedActivity.STILL -> ActivityType.STILL
                DetectedActivity.RUNNING -> ActivityType.RUNNING
                DetectedActivity.ON_BICYCLE -> ActivityType.ON_BICYCLE
                DetectedActivity.IN_VEHICLE -> ActivityType.IN_VEHICLE
                else -> ActivityType.UNKNOWN
            }

            _activityDataFlow.tryEmit(
                ActivityData(
                    timestamp = System.currentTimeMillis(),
                    type = type,
                    confidence = detectedActivity.confidence
                )
            )
        }
    }
}
