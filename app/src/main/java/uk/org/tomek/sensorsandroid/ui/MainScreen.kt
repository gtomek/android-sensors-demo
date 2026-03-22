package uk.org.tomek.sensorsandroid.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import uk.org.tomek.sensorsandroid.ui.model.MainUiState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val sensorsPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.values.all { it }) {
            viewModel.startSensors()
        }
        viewModel.onPermissionsRequested()
    }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is MainUiState.Error.Permissions) {
            sensorsPermissionLauncher.launch(state.permissions.toTypedArray())
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
                onStartSensorsClick = { viewModel.startSensors() },
                onStopSensorsClick = { viewModel.stopSensors() },
                onChangeDisplayTypeClick = { viewModel.changeDisplayType() },
                onGetCurrentLocation = { viewModel.getCurrentLocation() },
                onLocationMessageShown = { viewModel.onLocationMessageShown() },
                modifier = modifier
            )
        }

        is MainUiState.Error.Permissions -> {
            // While permissions are being requested, we can show a placeholder or the last known data
            // For now, let's just show a loading indicator or keep the previous UI if possible.
            // Since we're in a 'when' over state, we must show something.
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MainUiState.Error.Generic -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message)
            }
        }
    }
}
