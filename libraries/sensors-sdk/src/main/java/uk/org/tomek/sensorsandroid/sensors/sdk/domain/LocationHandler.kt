package uk.org.tomek.sensorsandroid.sensors.sdk.domain

import android.location.Location

interface LocationHandler {
    fun getLastKnownLocation(): Result<Location>
}
