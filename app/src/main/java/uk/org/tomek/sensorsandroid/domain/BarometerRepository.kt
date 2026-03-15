package uk.org.tomek.sensorsandroid.domain

import kotlinx.coroutines.flow.Flow
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.BarometerData

interface BarometerRepository {
    val barometerDataFlow: Flow<BarometerData>

    fun startListening() : Result<Unit>
    fun stopListening()
}
