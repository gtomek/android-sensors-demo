package uk.org.tomek.sensorsandroid.di

import android.hardware.SensorEventListener
import org.koin.dsl.module
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorListener

object KoinModule {
    val appModule = module {
        single<SensorEventListener> { SensorListener(get(), get()) }

    }
}