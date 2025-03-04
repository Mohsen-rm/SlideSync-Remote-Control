package com.appslidesyncremotecontrol

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.appslidesyncremotecontrol.ui.theme.AppSlideSyncRemoteTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemoteControlApp()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                // عند الضغط على زر رفع الصوت: الشريحة التالية
                sendCommand("next_slide")
                true  // منع تغيير مستوى الصوت الافتراضي
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                // عند الضغط على زر خفض الصوت: الشريحة السابقة
                sendCommand("prev_slide")
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlApp() {
    AppSlideSyncRemoteTheme {
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Remote Control App") })
                }
            ) { innerPadding ->
                RemoteControlContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun RemoteControlContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { sendCommand("next_slide") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Next Slide")
        }
        Button(
            onClick = { sendCommand("prev_slide") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Previous Slide")
        }
    }
}

fun sendCommand(command: String) {
    val url = "http://192.168.0.115:5000/$command"
    val client = OkHttpClient()
    // إرسال طلب POST بدون بيانات
    val request = Request.Builder()
        .url(url)
        .post(RequestBody.create(null, ByteArray(0)))
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Failed to send command: $e")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                println("Response from server: ${response.body?.string()}")
            } else {
                println("Failed to send command: ${response.code}")
            }
        }
    })
}