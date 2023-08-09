package me.brisson.speedtest.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.brisson.speedtest.ui.theme.SpeedTestTheme

@Composable
fun SpeedItem(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    speed: String
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        title()
        Text(text = speed, fontSize = 66.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SpeedItemTitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
//        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null)
        Text(text = title, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSpeedItem() {
    SpeedTestTheme {
        SpeedItem(
            title = {
                SpeedItemTitle(title = "DOWNLOAD")
            },
            speed = "100",
        )
    }
}