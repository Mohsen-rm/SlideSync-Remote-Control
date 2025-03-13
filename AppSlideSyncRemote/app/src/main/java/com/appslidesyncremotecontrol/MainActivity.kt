package com.appslidesyncremotecontrol

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.appslidesyncremotecontrol.ui.theme.AppSlideSyncRemoteTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    // المتغير لتخزين عنوان السيرفر المكتشف
    var remoteControlUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RemoteControlApp { url ->
                remoteControlUrl = url
                Log.d("RemoteControl", "Server found: $url")
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                sendCommand("next_slide")
                true  // منع تغيير مستوى الصوت الافتراضي
            }
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                sendCommand("prev_slide")
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoteControlApp(onServerFound: (String?) -> Unit) {
    AppSlideSyncRemoteTheme {
        MaterialTheme {
            Scaffold(
                topBar = {
                    TopAppBar(title = { Text("Remote Control App") })
                }
            ) { innerPadding ->
                RemoteControlContent(modifier = Modifier.padding(innerPadding), onServerFound)
            }
        }
    }
}

@Composable
fun RemoteControlContent(modifier: Modifier = Modifier, onServerFound: (String?) -> Unit) {
    val context = LocalContext.current
    var ipAddress by remember { mutableStateOf("جارٍ التحميل...") }
    var serverUrl by remember { mutableStateOf<String?>(null) }
    var discoveryMessage by remember { mutableStateOf("") }
    var discoveryFailed by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // دالة لإعادة المحاولة
    fun runDiscovery() {
        discoveryFailed = false
        discoveryMessage = "جارٍ الكشف عن السيرفر..."
        coroutineScope.launch {
            ipAddress = getIPAddress(context)
            val foundUrl = findRemoteControlServer(context, ipAddress)
            ServerConfig.remoteControlUrl = foundUrl
            serverUrl = foundUrl
            onServerFound(foundUrl)
            if (foundUrl != null) {
                discoveryMessage = "تم اكتشاف السيرفر: $foundUrl"
                // بعد 3 ثوانٍ تختفي الرسالة
                delay(3000)
                discoveryMessage = ""
            } else {
                discoveryMessage = "لم يتم العثور على السيرفر."
                discoveryFailed = true
            }
        }
    }

    // بدء عملية الكشف عند تحميل الـ Composable
    LaunchedEffect(Unit) {
        runDiscovery()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // القسم العلوي لعرض المعلومات
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "IP Address: $ipAddress", modifier = Modifier.padding(8.dp))
            if (discoveryMessage.isNotEmpty()) {
                Text(text = discoveryMessage, modifier = Modifier.padding(8.dp))
            }
            if (discoveryFailed) {
                Button(
                    onClick = { runDiscovery() },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("إعادة المحاولة")
                }
            }
        }

        // Spacer لدفع الأزرار إلى منتصف الشاشة
        Spacer(modifier = Modifier.weight(1f))

        // صف الأزرار في منتصف الشاشة
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { sendCommand("next_slide") },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
            ) {
                Text("Next Slide")
            }
            Button(
                onClick = { sendCommand("prev_slide") },
                modifier = Modifier
                    .weight(1f)
                    .height(60.dp)
            ) {
                Text("Previous Slide")
            }
        }

        // Spacer إضافي إذا رغبت في وجود مسافة في الأسفل
        Spacer(modifier = Modifier.weight(1f))
    }
}

fun getIPAddress(context: Context): String {
    val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val wifiInfo = wifiManager.connectionInfo
    val ip = wifiInfo.ipAddress
    return String.format(
        Locale.US, "%d.%d.%d.%d",
        ip and 0xff,
        ip shr 8 and 0xff,
        ip shr 16 and 0xff,
        ip shr 24 and 0xff
    )
}

// دالة بحث عن السيرفر عبر الشبكة مع طباعات للتتبع
suspend fun findRemoteControlServer(context: Context, deviceIp: String): String? {
    val parts = deviceIp.split(".")
    if (parts.size != 4) return null
    val prefix = parts.take(3).joinToString(".")  // مثل "192.168.0"
    // إنشاء عميل مع timeouts قصيرة
    val client = OkHttpClient.Builder()
        .connectTimeout(500, TimeUnit.MILLISECONDS)
        .readTimeout(500, TimeUnit.MILLISECONDS)
        .build()

    return withContext(Dispatchers.IO) {
        val deferredResults = (1..224).map { i ->
            async {
                val testIp = "$prefix.$i"
                val url = "http://$testIp:5000/"
                Log.d("RemoteControl", "فحص: $url")
                try {
                    val response = client.newCall(Request.Builder().url(url).build()).execute()
                    if (response.isSuccessful) {
                        val body = response.body?.string() ?: ""
                        Log.d("RemoteControl", "الرد من $url: $body")
                        if (body.contains("\"AppSlideSyncRemote\": true") &&
                            body.contains("\"port\": 57875")
                        ) {
                            url  // السيرفر المطلوب
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.d("RemoteControl", "خطأ عند فحص $url: ${e.message}")
                    null
                }
            }
        }
        deferredResults.awaitAll().firstOrNull { it != null }
    }
}

object ServerConfig {
    var remoteControlUrl: String? = null
}

fun sendCommand(command: String) {
    // استخدام عنوان السيرفر المكتشف إذا كان موجودًا، وإلا استخدام عنوان افتراضي
    val baseUrl = ServerConfig.remoteControlUrl ?: "http://192.168.0.115:5000"
    val url = "$baseUrl/$command"
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .post(RequestBody.create(null, ByteArray(0)))
        .build()

    client.newCall(request).enqueue(object : okhttp3.Callback {
        override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
            Log.d("RemoteControl", "فشل إرسال الأمر: $e")
        }

        override fun onResponse(call: okhttp3.Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("RemoteControl", "الرد من السيرفر: ${response.body?.string()}")
            } else {
                Log.d("RemoteControl", "فشل إرسال الأمر: ${response.code}")
            }
        }
    })
}
