package me.brisson.speedtest.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import me.brisson.speedtest.R
import me.brisson.speedtest.ui.theme.SpeedTestTheme

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (uiState.isConnected) {
            viewModel.handleEvents(HomeEvent.FetchNetworkDetails)
        }
    }

    HomeScreen(modifier, uiState)
}

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    uiState: HomeState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val text = if (uiState.isConnected) {
                if (!uiState.ipAddress.isNullOrEmpty()) {
                    "IP: ${uiState.ipAddress}"
                } else {
                    "Connected"
                }
            } else {
                "Disconnected"
            }

            Text(
                text = stringResource(id = R.string.app_name),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = text,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.padding(40.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            if (uiState.ping > -1) {
                SpeedTestTheme {
                    SpeedItem(
                        title = {
                            SpeedItemTitle(title = "PING")
                        },
                        speed = uiState.ping.toInt().toString(),
                    )
                }
            }

            if (uiState.downloadSpeed > -1) {
                SpeedTestTheme {
                    SpeedItem(
                        title = {
                            SpeedItemTitle(title = "DOWNLOAD")
                        },
                        speed = uiState.downloadSpeed.toString(),
                    )
                }
            }

            if (uiState.uploadSpeed > -1) {
                SpeedTestTheme {
                    SpeedItem(
                        title = {
                            SpeedItemTitle(title = "UPLOAD")
                        },
                        speed = uiState.uploadSpeed.toString(),
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    SpeedTestTheme {
        val uiState = HomeState(downloadSpeed = 100, uploadSpeed = 80, ping = 8.0)
        HomeScreen(uiState = uiState)
    }
}
