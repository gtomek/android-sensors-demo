package uk.org.tomek.sensorsandroid.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.org.tomek.sensorsandroid.data.BleScanRepositoryDefault
import uk.org.tomek.sensorsandroid.data.LocationRepositoryDefault
import uk.org.tomek.sensorsandroid.data.SensorsRepositoryDefault
import uk.org.tomek.sensorsandroid.data.WifiScanRepositoryDefault
import uk.org.tomek.sensorsandroid.domain.BleScanRepository
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.domain.WifiScanRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.SensorsSdk
import uk.org.tomek.sensorsandroid.ui.MainViewModel
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper

object KoinModule {
    val appModule = module {
        single { SensorDomainUiMapper() }
        single { SensorsSdk(get()) }
        single { SensorsRepositoryDefault(get()) } bind SensorsRepository::class
        single { LocationRepositoryDefault(get()) } bind LocationRepository::class
        single { WifiScanRepositoryDefault(get()) } bind WifiScanRepository::class
        single { BleScanRepositoryDefault(get()) } bind BleScanRepository::class
        viewModel { MainViewModel(get(), get(), get(), get(), get()) }
    }
}
