package uk.org.tomek.sensorsandroid.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.org.tomek.sensorsandroid.data.DeviceInfoRepositoryDefault
import uk.org.tomek.sensorsandroid.data.LocationRepositoryDefault
import uk.org.tomek.sensorsandroid.data.SensorsRepositoryDefault
import uk.org.tomek.sensorsandroid.domain.DeviceInfoRepository
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkConfig
import uk.org.tomek.sensorsandroid.ui.MainViewModel
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper

object KoinModule {
    val appModule = module {
        single { SensorDomainUiMapper() }
        single { SensorsSdk(get(), SensorsSdkConfig()) }
        single { SensorsRepositoryDefault(get()) } bind SensorsRepository::class
        single { LocationRepositoryDefault(get()) } bind LocationRepository::class
        single { DeviceInfoRepositoryDefault(get()) } bind DeviceInfoRepository::class
        viewModel { MainViewModel(get(), get(), get(), get()) }
    }
}
