package uk.org.tomek.sensorsandroid.ui

import androidx.lifecycle.ViewModel
import org.koin.android.annotation.KoinViewModel
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
): ViewModel() {

    fun startSensors() = sensorsRepository.startSensors()
    fun stopSensors() = sensorsRepository.stopLeasingSensors()
}