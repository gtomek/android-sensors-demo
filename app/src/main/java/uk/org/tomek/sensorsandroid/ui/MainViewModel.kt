package uk.org.tomek.sensorsandroid.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.koin.android.annotation.KoinViewModel
import timber.log.Timber
import uk.org.tomek.sensorsandroid.domain.DeviceInfoRepository
import uk.org.tomek.sensorsandroid.domain.LocationRepository
import uk.org.tomek.sensorsandroid.domain.SensorsRepository
import uk.org.tomek.sensorsandroid.sensors.sdk.domain.model.SensorsSdkResult
import uk.org.tomek.sensorsandroid.ui.mapper.SensorDomainUiMapper
import uk.org.tomek.sensorsandroid.ui.model.BleDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.DisplayType
import uk.org.tomek.sensorsandroid.ui.model.MainUiState

@KoinViewModel
class MainViewModel(
    private val sensorsRepository: SensorsRepository,
    private val locationRepository: LocationRepository,
    private val deviceInfoRepository: DeviceInfoRepository,
    private val sensorDataMapper: SensorDomainUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Loading)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val displayType = MutableStateFlow(DisplayType.LIST)

    init {
        // Initialize with empty data after "Loading"
        _uiState.value = MainUiState.Data(
            sensorData = emptyList(),
            deviceInfo = sensorDataMapper.toUi(deviceInfoRepository.getDeviceInfo())
        )

        sensorsRepository.scanResults
            .onEach { result ->
                if (result is SensorsSdkResult.SuccessEvent) {
                    _uiState.update { currentState ->
                        if (currentState is MainUiState.Data) {
                            var newState: MainUiState.Data = currentState

                            result.sensor?.let { sensorData ->
                                Timber.v("Sensor data: $sensorData")
                                val uiModel = sensorDataMapper.toUi(sensorData)
                                val currentList = newState.sensorData
                                val newList = if (displayType.value == DisplayType.LAST_ONLY) {
                                    val updatedList = currentList.toMutableList()
                                    val index = updatedList.indexOfFirst { it.sensorName == uiModel.sensorName }
                                    if (index != -1) {
                                        updatedList[index] = uiModel
                                    } else {
                                        updatedList.add(uiModel)
                                    }
                                    updatedList
                                } else {
                                    currentList + uiModel
                                }
                                newState = newState.copy(sensorData = newList.sortedBy { it.sensorType })
                            }

                            result.wifiData?.let { wifiData ->
                                Timber.v("WiFi data: $wifiData")
                                val uiModel = sensorDataMapper.toUi(wifiData)
                                val currentList = newState.wifiData
                                val updatedList = currentList.toMutableList()
                                val index = updatedList.indexOfFirst { it.bssid == uiModel.bssid }
                                if (index != -1) {
                                    updatedList[index] = uiModel
                                } else {
                                    updatedList.add(uiModel)
                                }
                                newState = newState.copy(wifiData = updatedList.sortedByDescending { it.rssi })
                            }

                            result.bleData?.let { bleData ->
                                Timber.v("BLE data: $bleData")
                                val uiModel = sensorDataMapper.toUi(bleData)
                                val currentList = newState.bleData
                                val updatedList = currentList.toMutableList()
                                val index = updatedList.indexOfFirst { it.deviceAddress == uiModel.deviceAddress }
                                if (index != -1) {
                                    updatedList[index] = uiModel
                                } else {
                                    updatedList.add(uiModel)
                                }

                                // Sort: Prioritized device first, then by RSSI
                                val sortedList = updatedList.sortedWith(
                                    compareByDescending<BleDataUiModel> {
                                        it.deviceAddress.equals("D0:62:2C:89:A8:29", ignoreCase = true)
                                    }.thenByDescending { it.rssi }
                                )
                                newState = newState.copy(bleData = sortedList)
                            }

                            result.mobileNetworkData?.let { mobileNetworkData ->
                                Timber.v("Mobile Network data: $mobileNetworkData")
                                val uiModel = sensorDataMapper.toUi(mobileNetworkData)
                                newState = newState.copy(mobileNetworkData = uiModel)
                            }

                            result.barometerData?.let { barometerData ->
                                Timber.v("Barometer data: $barometerData")
                                val uiModel = sensorDataMapper.toUi(barometerData)
                                newState = newState.copy(barometerData = uiModel)
                            }

                            result.activityData?.let { activityData ->
                                Timber.v("Activity data: $activityData")
                                val uiModel = sensorDataMapper.toUi(activityData)
                                newState = newState.copy(activityData = uiModel)
                            }

                            newState
                        } else {
                            currentState
                        }
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun startSensors() {
        sensorsRepository.start()

        // Refresh device info when starting sensors to catch context changes
        _uiState.update { currentState ->
            if (currentState is MainUiState.Data) {
                currentState.copy(deviceInfo = sensorDataMapper.toUi(deviceInfoRepository.getDeviceInfo()))
            } else {
                currentState
            }
        }
    }

    fun stopSensors() {
        sensorsRepository.stop()
    }

    fun changeDisplayType() {
        displayType.update {
            if (it == DisplayType.LIST) DisplayType.LAST_ONLY else DisplayType.LIST
        }
        if (displayType.value == DisplayType.LAST_ONLY) {
            _uiState.update { currentState ->
                if (currentState is MainUiState.Data) {
                    val filteredList = currentState.sensorData.groupBy { it.sensorName }
                        .map { it.value.last() }
                        .sortedBy { it.sensorType }
                    currentState.copy(sensorData = filteredList)
                } else {
                    currentState
                }
            }
        }
    }

    fun getCurrentLocation() {
        locationRepository.getLastKnownLocation()
            .onSuccess { location ->
                updateLocationMessage("Location: ${location.latitude}, ${location.longitude}")
            }
            .onFailure { error ->
                updateLocationMessage("Error: ${error.message}")
            }
    }

    private fun updateLocationMessage(message: String) {
        _uiState.update { currentState ->
            if (currentState is MainUiState.Data) {
                currentState.copy(locationMessage = message)
            } else {
                currentState
            }
        }
    }

    fun onLocationMessageShown() {
        _uiState.update { currentState ->
            if (currentState is MainUiState.Data) {
                currentState.copy(locationMessage = null)
            } else {
                currentState
            }
        }
    }
}
