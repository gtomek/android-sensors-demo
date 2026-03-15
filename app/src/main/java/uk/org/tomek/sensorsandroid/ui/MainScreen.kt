package uk.org.tomek.sensorsandroid.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import org.koin.androidx.compose.koinViewModel
import uk.org.tomek.sensorsandroid.ui.model.MainUiState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { _ ->
        viewModel.getCurrentLocation()
    }

    when (val state = uiState) {
        is MainUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MainUiState.Data -> {
            MainScreenData(
                state = state,
                onStartSensorsClick = { viewModel.startSensors() },
                onStopSensorsClick = { viewModel.stopSensors() },
                onChangeDisplayTypeClick = { viewModel.changeDisplayType() },
                onGetCurrentLocation = {
                    val fineLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val coarseLocationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                    if (fineLocationPermission == PackageManager.PERMISSION_GRANTED ||
                        coarseLocationPermission == PackageManager.PERMISSION_GRANTED
                    ) {
                        viewModel.getCurrentLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onLocationMessageShown = { viewModel.onLocationMessageShown() },
                modifier = modifier
            )
        }
    }
}
