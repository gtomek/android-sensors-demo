package uk.org.tomek.sensorsandroid.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
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

    val locationPermissions = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.NEARBY_WIFI_DEVICES)
        }
    }.toTypedArray()

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    val sensorsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.startSensors()
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.any { it }) {
            viewModel.getCurrentLocation()
        }
    }

    fun runWithPermission(
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
        action: () -> Unit
    ) {
        if (hasLocationPermission()) {
            action()
        } else {
            launcher.launch(locationPermissions)
        }
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
                onStartSensorsClick = {
                    runWithPermission(sensorsPermissionLauncher) { viewModel.startSensors() }
                },
                onStopSensorsClick = { viewModel.stopSensors() },
                onChangeDisplayTypeClick = { viewModel.changeDisplayType() },
                onGetCurrentLocation = {
                    runWithPermission(locationPermissionLauncher) { viewModel.getCurrentLocation() }
                },
                onLocationMessageShown = { viewModel.onLocationMessageShown() },
                modifier = modifier
            )
        }
    }
}
