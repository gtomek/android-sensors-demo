package uk.org.tomek.sensorsandroid.data

import android.location.Location
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk

class LocationRepositoryDefault(private val sensorsSdk: SensorsSdk) : LocationRepository {
    override fun getLastKnownLocation(): Result<Location> {
        return sensorsSdk.getLastKnownLocation()
    }
}
