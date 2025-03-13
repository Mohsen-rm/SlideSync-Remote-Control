import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.alkafeel.questions.R

@Composable
fun MusicControl(soundManager: SoundManager) {
    var isPlaying by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }

    // لإختيار ملف صوتي من الجهاز
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            soundManager.playMusicFromUri(it)
            isPlaying = true
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "تحكم الموسيقى الخلفية", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { launcher.launch("audio/*") }) {
                Text("اختر من الجهاز")
            }
            Button(onClick = {
                // افترض وجود ملف موسيقى في مجلد raw بالاسم my_music
                soundManager.playMusicFromRaw(R.raw.tension_suspense_music)
                isPlaying = true
            }) {
                Text("تشغيل من raw")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                if (isPlaying) {
                    soundManager.stopMusic()
                    isPlaying = false
                } else {
                    // إعادة تشغيل الموسيقى، إذا كان هناك اختيار من الجهاز نستخدمه، وإلا نستخدم raw
                    if (selectedUri != null) {
                        soundManager.playMusicFromUri(selectedUri!!)
                    } else {
                        soundManager.playMusicFromRaw(R.raw.tutur_i_want_you)
                    }
                    isPlaying = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = if (isPlaying) "إيقاف الموسيقى" else "تشغيل الموسيقى")
        }
    }
}
