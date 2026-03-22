package uk.org.tomek.sensorsandroid.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module
import uk.org.tomek.sensorsandroid.data.ActivityRepositoryDefault
import uk.org.tomek.sensorsandroid.data.BarometerRepositoryDefault
import uk.org.tomek.sensorsandroid.data.BleScanRepositoryDefault
import uk.org.tomek.sensorsandroid.data.DeviceInfoRepositoryDefault
import uk.org.tomek.sensorsandroid.data.LocationRepositoryDefault
import uk.org.tomek.sensorsandroid.data.MobileNetworksRepositoryDefault
import uk.org.tomek.sensorsandroid.data.SensorsRepositoryDefault
import uk.org.tomek.sensorsandroid.data.WifiScanRepositoryDefault
import uk.org.tomek.sensorsandroid.domain.ActivityRepository
import uk.org.tomek.sensorsandroid.domain.BarometerRepository
import uk.org.tomek.sensorsandroid.domain.BleScanRepository
import uk.org.tomek.sensorsandroid.domain.DeviceInfoRepository
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.domain.MobileNetworksRepository
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.domain.WifiScanRepository
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
        single { WifiScanRepositoryDefault(get()) } bind WifiScanRepository::class
        single { BleScanRepositoryDefault(get()) } bind BleScanRepository::class
        single { MobileNetworksRepositoryDefault(get()) } bind MobileNetworksRepository::class
        single { BarometerRepositoryDefault(get()) } bind BarometerRepository::class
        single { DeviceInfoRepositoryDefault(get()) } bind DeviceInfoRepository::class
        single { ActivityRepositoryDefault(get()) } bind ActivityRepository::class
        viewModel { MainViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    }
}
