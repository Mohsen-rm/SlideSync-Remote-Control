package com.appslidesyncremotecontrol

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlApp(onCommandSend: (String) -> Unit) {
    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Remote Control") })
            }
        ) { innerPadding ->
            RemoteControlContent(modifier = Modifier.padding(innerPadding), onCommandSend)
        }
    }
}

@Composable
fun RemoteControlContent(modifier: Modifier = Modifier, onCommandSend: (String) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { onCommandSend("next_slide") }, modifier = Modifier.padding(8.dp)) {
            Text("Next Slide")
        }
        Button(onClick = { onCommandSend("prev_slide") }, modifier = Modifier.padding(8.dp)) {
            Text("Previous Slide")
        }
    }
}
