package uk.org.tomek.sensorsandroid.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import uk.org.tomek.sensorsandroid.ui.model.ActivityDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.BarometerDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.BleDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.CellInfoUiModel
import uk.org.tomek.sensorsandroid.ui.model.DeviceInfoUiModel
import uk.org.tomek.sensorsandroid.ui.model.MainUiState
import uk.org.tomek.sensorsandroid.ui.model.MobileNetworkDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel
import uk.org.tomek.sensorsandroid.ui.model.WifiDataUiModel
import uk.org.tomek.sensorsandroid.ui.theme.Pink40
import uk.org.tomek.sensorsandroid.ui.theme.SensorsAndroidTheme
import kotlin.time.Instant

@Composable
fun MainScreenData(
    state: MainUiState.Data,
    onStartSensorsClick: () -> Unit,
    onStopSensorsClick: () -> Unit,
    onChangeDisplayTypeClick: () -> Unit,
    onGetCurrentLocation: () -> Unit,
    onLocationMessageShown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.locationMessage) {
        state.locationMessage?.let {
            snackBarHostState.showSnackbar(it)
            onLocationMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        DefaultMainScreenContent(
            sensorData = state.sensorData,
            wifiData = state.wifiData,
            bleData = state.bleData,
            mobileNetworkData = state.mobileNetworkData,
            barometerData = state.barometerData,
            activityData = state.activityData,
            deviceInfo = state.deviceInfo,
            onStartSensorsClick = onStartSensorsClick,
            onStopSensorsClick = onStopSensorsClick,
            onChangeDisplayTypeClick = onChangeDisplayTypeClick,
            onGetCurrentLocation = onGetCurrentLocation,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DefaultMainScreenContent(
    sensorData: List<SensorDataUiModel>,
    wifiData: List<WifiDataUiModel>,
    bleData: List<BleDataUiModel>,
    mobileNetworkData: MobileNetworkDataUiModel?,
    barometerData: BarometerDataUiModel?,
    activityData: ActivityDataUiModel?,
    deviceInfo: DeviceInfoUiModel?,
    onStartSensorsClick: () -> Unit,
    onStopSensorsClick: () -> Unit,
    onChangeDisplayTypeClick: () -> Unit,
    onGetCurrentLocation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedSensorData = remember(sensorData) {
        sensorData.groupBy { it.sensorName }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {

        Text(
            text = "Android sensors demo",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            style = MaterialTheme.typography.headlineMedium
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onStartSensorsClick()
            }
        ) {
            Text(
                text = "Start sensors"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onStopSensorsClick()
            }
        ) {
            Text(
                text = "Stop sensors"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onGetCurrentLocation() }
        ) {
            Text(
                text = "Get current location"
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Pink40),
            onClick = {
                onChangeDisplayTypeClick()
            }
        ) {
            Text(
                text = "Change display type"
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            if (deviceInfo != null) {
                stickyHeader {
                    Text(
                        text = "Device Information",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFBBDEFB))
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF0D47A1)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(text = "Manufacturer: ${deviceInfo.manufacturer}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Model: ${deviceInfo.model}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Android: ${deviceInfo.androidVersion}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Battery: ${deviceInfo.batteryInfo}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Screen: ${deviceInfo.screenState}, Orientation: ${deviceInfo.orientation}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Power Save: ${deviceInfo.powerSaveMode}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Foreground App: ${deviceInfo.foregroundApp}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Sensors detected: ${deviceInfo.sensorCount}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            groupedSensorData.forEach { (sensorName, sensors) ->
                stickyHeader {
                    Text(
                        text = sensorName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                items(sensors) { sensor ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = sensor.sensorStringType,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Values: ${sensor.sensorValues.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Timestamp: ${sensor.sensorTimestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (barometerData != null) {
                stickyHeader {
                    Text(
                        text = "Barometer Info (Floor Detection)",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF9C4))
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFFFBC02D)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Pressure: ${barometerData.pressure}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Est. Altitude: ${barometerData.altitude}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Accuracy: ${barometerData.accuracy}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Timestamp: ${barometerData.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (mobileNetworkData != null) {
                stickyHeader {
                    Text(
                        text = "Mobile Network Info",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE1BEE7))
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF4A148C)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Timestamp: ${mobileNetworkData.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "Primary Cell",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        mobileNetworkData.primaryCell?.let { CellInfoItem(it) } ?: Text("No primary cell info", style = MaterialTheme.typography.bodySmall)

                        if (mobileNetworkData.neighboringCells.isNotEmpty()) {
                            Text(
                                text = "Neighboring Cells",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            mobileNetworkData.neighboringCells.forEach {
                                CellInfoItem(it)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }

            if (wifiData.isNotEmpty()) {
                stickyHeader {
                    Text(
                        text = "WiFi Scan Results",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }

                items(wifiData) { wifi ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${wifi.ssid} (${wifi.bssid})",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "RSSI: ${wifi.rssi} dBm, Freq: ${wifi.frequency} MHz",
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (wifi.distance != null) {
                            Text(
                                text = "Distance: ${wifi.distance}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Text(
                            text = "Capabilities: ${wifi.capabilities}",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Timestamp: ${wifi.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (bleData.isNotEmpty()) {
                stickyHeader {
                    Text(
                        text = "BLE Scan Results",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                items(bleData) { ble ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${ble.deviceName ?: "Unknown"} (${ble.deviceAddress})",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "RSSI: ${ble.rssi} dBm" + (ble.txPower?.let { ", TxPower: $it dBm" } ?: ""),
                            style = MaterialTheme.typography.bodySmall
                        )
                        if (ble.beaconInfo != null) {
                            Text(
                                text = ble.beaconInfo,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Text(
                            text = "Timestamp: ${ble.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            if (activityData != null) {
                stickyHeader {
                    Text(
                        text = "Activity Recognition",
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFC8E6C9))
                            .padding(8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2E7D32)
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Detected Activity: ${activityData.type}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Confidence: ${activityData.confidence}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Timestamp: ${activityData.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CellInfoItem(cell: CellInfoUiModel) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = "Type: ${cell.type}, ID: ${cell.cellId}, LAC/TAC: ${cell.lacTac}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "MCC/MNC: ${cell.mccMnc}, Strength: ${cell.signalStrength}, TA: ${cell.timingAdvance}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenDataPreview() {
    SensorsAndroidTheme {
        MainScreenData(
            state = MainUiState.Data(
                sensorData = listOf(
                    SensorDataUiModel(
                        sensorType = 1,
                        sensorStringType = "Accelerometer",
                        sensorName = "Standard Accelerometer",
                        sensorTimestamp = Instant.fromEpochMilliseconds(1739800000000L),
                        sensorValues = listOf(0.1f, 9.8f, 0.5f)
                    )
                ),
                deviceInfo = DeviceInfoUiModel(
                    model = "Pixel 7",
                    manufacturer = "Google",
                    androidVersion = "14 (API 34)",
                    batteryInfo = "85% (Charging)",
                    screenState = "On",
                    orientation = "Portrait",
                    powerSaveMode = "Disabled",
                    foregroundApp = "uk.org.tomek.sensorsandroid",
                    sensorCount = 42
                ),
                barometerData = BarometerDataUiModel(
                    pressure = "1013.25 hPa",
                    altitude = "10.5 m",
                    accuracy = "3",
                    timestamp = "12:00:06.000"
                ),
                mobileNetworkData = MobileNetworkDataUiModel(
                    timestamp = "12:00:05.000",
                    primaryCell = CellInfoUiModel("LTE", "12345", "6789", "260/02", "-95 dBm", "5"),
                    neighboringCells = listOf(
                        CellInfoUiModel("LTE", "12346", "6789", "260/02", "-105 dBm", "N/A")
                    )
                ),
                wifiData = listOf(
                    WifiDataUiModel(
                        ssid = "Home_Network",
                        bssid = "00:11:22:33:44:55",
                        rssi = -45,
                        frequency = 5240,
                        capabilities = "[WPA2-PSK-CCMP][RSN-PSK-CCMP][ESS]",
                        distance = "2.5m (±0.3m)",
                        timestamp = "12:00:00.000"
                    )
                ),
                bleData = listOf(
                    BleDataUiModel(
                        deviceAddress = "AA:BB:CC:DD:EE:FF",
                        deviceName = "Beacon_01",
                        rssi = -55,
                        txPower = -12,
                        beaconInfo = "iBeacon: UUID=..., Major=1, Minor=2",
                        timestamp = "12:00:02.000"
                    )
                ),
                activityData = ActivityDataUiModel(
                    type = "WALKING",
                    confidence = "95",
                    timestamp = "12:00:08.000"
                ),
                locationMessage = "Location: 52.0, 0.0"
            ),
            onStartSensorsClick = {},
            onStopSensorsClick = {},
            onChangeDisplayTypeClick = {},
            onGetCurrentLocation = {},
            onLocationMessageShown = {}
        )
    }
}
