package uk.org.tomek.sensorsandroid.domain

import android.location.Location

interface LocationRepository {

    fun getLastKnownLocation() : Result<Location>
}