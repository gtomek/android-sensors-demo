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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel
import uk.org.tomek.sensorsandroid.ui.model.SensorDataUiModel
import uk.org.tomek.sensorsandroid.ui.theme.Pink40
import uk.org.tomek.sensorsandroid.ui.theme.SensorsAndroidTheme
import kotlin.time.Instant

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DefaultMainScreen(
        data = uiState,
        onStartSensorsClick = { viewModel.startSensors() },
        onStopSensorsClick = { viewModel.stopSensors() },
        onChangeDisplayTypeClick = { viewModel.changeDisplayType() },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DefaultMainScreen(
    data: List<SensorDataUiModel>,
    onStartSensorsClick: () -> Unit,
    onStopSensorsClick: () -> Unit,
    onChangeDisplayTypeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val groupedData = remember(data) {
        data.groupBy { it.sensorName }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
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
            groupedData.forEach { (sensorName, sensors) ->
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    SensorsAndroidTheme {
        DefaultMainScreen(
            data = emptyList(),
            onStartSensorsClick = {},
            onStopSensorsClick = {},
            onChangeDisplayTypeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenWithDataPreview() {
    SensorsAndroidTheme {
        DefaultMainScreen(
            data = listOf(
                SensorDataUiModel(
                    sensorType = 1,
                    sensorStringType = "Accelerometer",
                    sensorName = "Standard Accelerometer",
                    sensorTimestamp = Instant.fromEpochMilliseconds(1739800000000L),
                    sensorValues = listOf(0.1f, 9.8f, 0.5f)
                ),
                SensorDataUiModel(
                    sensorType = 1,
                    sensorStringType = "Accelerometer",
                    sensorName = "Standard Accelerometer",
                    sensorTimestamp = Instant.fromEpochMilliseconds(1739800001000L),
                    sensorValues = listOf(0.2f, 9.7f, 0.4f)
                ),
                SensorDataUiModel(
                    sensorType = 4,
                    sensorStringType = "Gyroscope",
                    sensorName = "Standard Gyroscope",
                    sensorTimestamp = Instant.fromEpochMilliseconds(1739800000500L),
                    sensorValues = listOf(0.01f, 0.02f, 0.03f)
                )
            ),
            onStartSensorsClick = {},
            onStopSensorsClick = {},
            onChangeDisplayTypeClick = {}
        )
    }
}
