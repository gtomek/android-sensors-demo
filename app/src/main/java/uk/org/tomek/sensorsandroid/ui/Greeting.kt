package uk.org.tomek.sensorsandroid.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import uk.org.tomek.sensorsandroid.ui.theme.SensorsAndroidTheme

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = koinViewModel()
) {
    DefaultGreeting(
        name = name,
        onStartSensorsClick = { viewModel.startSensors() },
        modifier = modifier
    )
}

@Composable
private fun DefaultGreeting(
    name: String,
    onStartSensorsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier.fillMaxSize()
    ) {

        Text(
            text = "Hello $name!",
            modifier = modifier
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

    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorsAndroidTheme {
        DefaultGreeting(
            "Android",
            onStartSensorsClick = {})
    }
}