//package com.appslidesyncremotecontrol
//
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import android.view.KeyEvent
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Switch
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.appslidesyncremotecontrol.ui.theme.AppSlideSyncRemoteTheme
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import okhttp3.Response
//import java.io.IOException
//
//class MainActivity : ComponentActivity() {
//
//    // حالة تفعيل التحكم بالحركة (Motion Control)
//    private var motionControlEnabled by mutableStateOf(false)
//    // لتجنب إرسال أوامر متكررة بشكل سريع
//    private var lastMotionCommandTime: Long = 0L
//    private lateinit var sensorManager: SensorManager
//    private lateinit var accelerometer: Sensor
//
//    // مستمع لمستشعر التسارع
//    private val sensorListener = object : SensorEventListener {
//        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }
//
//        override fun onSensorChanged(event: SensorEvent) {
//            if (!motionControlEnabled) return
//
//            val currentTime = System.currentTimeMillis()
//            // تأخير بسيط لتجنب التكرار (1 ثانية)
//            if (currentTime - lastMotionCommandTime < 1000) return
//
//            val x = event.values[0]
//            val threshold = 3.0f  // عتبة الميل
//            // في الوضع الرأسي: عندما يكون x أكبر من العتبة يعني أن الجهاز يميل إلى اليسار
//            if (x > threshold) {
//                sendCommand("prev_slide")
//                lastMotionCommandTime = currentTime
//            }
//            // عندما يكون x أقل من -threshold يعني أن الجهاز يميل إلى اليمين
//            else if (x < -threshold) {
//                sendCommand("next_slide")
//                lastMotionCommandTime = currentTime
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!!
//        sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
//
//        setContent {
//            RemoteControlApp(
//                motionControlEnabled = motionControlEnabled,
//                onMotionControlToggle = { motionControlEnabled = it }
//            )
//        }
//    }
//    override fun onDestroy() {
//        sensorManager.unregisterListener(sensorListener)
//        super.onDestroy()
//    }
//
//    // التقاط ضغطات أزرار الصوت
//    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
//        return when (keyCode) {
//            KeyEvent.KEYCODE_VOLUME_UP -> {
//                // عند الضغط على زر رفع الصوت: يتم تنفيذ أمر الشريحة التالية
//                sendCommand("next_slide")
//                true  // منع تغيير مستوى الصوت الافتراضي
//            }
//            KeyEvent.KEYCODE_VOLUME_DOWN -> {
//                // عند الضغط على زر خفض الصوت: يتم تنفيذ أمر الشريحة السابقة
//                sendCommand("prev_slide")
//                true
//            }
//            else -> super.onKeyDown(keyCode, event)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun RemoteControlApp(
//    motionControlEnabled: Boolean,
//    onMotionControlToggle: (Boolean) -> Unit
//) {
//    AppSlideSyncRemoteTheme {
//        MaterialTheme {
//            Scaffold(
//                topBar = {
//                    TopAppBar(title = { Text("Remote Control App") })
//                }
//            ) { innerPadding ->
//                RemoteControlContent(
//                    modifier = Modifier.padding(innerPadding),
//                    motionControlEnabled = motionControlEnabled,
//                    onMotionControlToggle = onMotionControlToggle
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun RemoteControlContent(
//    modifier: Modifier = Modifier,
//    motionControlEnabled: Boolean,
//    onMotionControlToggle: (Boolean) -> Unit
//) {
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(
//            onClick = { sendCommand("next_slide") },
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Text("Next Slide")
//        }
//        Button(
//            onClick = { sendCommand("prev_slide") },
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Text("Previous Slide")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.padding(8.dp)
//        ) {
//            Text("Motion Control")
//            Switch(
//                checked = motionControlEnabled,
//                onCheckedChange = onMotionControlToggle
//            )
//        }
//    }
//}
//
//fun sendCommand(command: String) {
//    val url = "http://192.168.0.115:5000/$command"
//    val client = OkHttpClient()
//    // إرسال طلب POST بدون بيانات
//    val request = Request.Builder()
//        .url(url)
//        .post(RequestBody.create(null, ByteArray(0)))
//        .build()
//
//    client.newCall(request).enqueue(object : Callback {
//        override fun onFailure(call: Call, e: IOException) {
//            println("Failed to send command: $e")
//        }
//
//        override fun onResponse(call: Call, response: Response) {
//            if (response.isSuccessful) {
//                println("Response from server: ${response.body?.string()}")
//            } else {
//                println("Failed to send command: ${response.code}")
//            }
//        }
//    })
//}