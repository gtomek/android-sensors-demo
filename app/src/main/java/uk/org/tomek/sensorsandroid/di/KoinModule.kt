package uk.org.tomek.sensorsandroid.di

import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.org.tomek.sensorsandroid.data.SensorsRepositoryDefault
import uk.org.tomek.sensorsandroid.data.mapper.SensorDataMapper
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorListener
import uk.org.tomek.sensorsandroid.ui.MainViewModel

object KoinModule {
    val appModule = module {
        single { SensorDataMapper() }
        single { SensorListener(get(), { _: SensorEvent -> }) } bind SensorEventListener::class
        single { SensorsRepositoryDefault(get(), get()) } bind SensorsRepository::class
        viewModel { MainViewModel(get()) }
    }
}
