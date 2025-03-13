import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.alkafeel.questions.R
import net.alkafeel.questions.SoundManager

data class MusicItem(val title: String, val resId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(onBack: () -> Unit) {
    var isPlaying by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var selectedRawMusic by remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }

    // لإختيار موسيقى من الجهاز
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedUri = it
            soundManager.playMusicFromUri(it)
            isPlaying = true
            selectedRawMusic = null
        }
    }

    // قائمة الموسيقى من مجلد raw
    val rawMusicList = listOf(
        MusicItem("موسيقى 1", R.raw.tension_suspense_music),
        MusicItem("موسيقى 2", R.raw.scary_suspense),
        MusicItem("موسيقى 3", R.raw.suspense_dramatic_epic_dark_anxious_emotional),
        MusicItem("موسيقى 4", R.raw.suspense_horror_tension),
        MusicItem("موسيقى 5", R.raw.tutur_i_want_you),
        MusicItem("موسيقى 6", R.raw.unspoken_tense_underscore)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("موسيقى الخلفية") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painter = painterResource(id = R.drawable.left_chevron), contentDescription = "رجوع")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // زر تشغيل/إيقاف الموسيقى
            Button(
                onClick = {
                    if (isPlaying) {
                        soundManager.stopMusic()
                        isPlaying = false
                    } else {
                        if (selectedUri != null) {
                            soundManager.playMusicFromUri(selectedUri!!)
                            isPlaying = true
                        } else if (selectedRawMusic != null) {
                            soundManager.playMusicFromRaw(selectedRawMusic!!)
                            isPlaying = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (isPlaying) "إيقاف الموسيقى" else "تشغيل الموسيقى")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // زر اختيار موسيقى من الجهاز
            Button(
                onClick = { launcher.launch("audio/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("اختر موسيقى من الجهاز")
            }
            Spacer(modifier = Modifier.height(16.dp))
            // قائمة موسيقى من مجلد raw
            Text(text = "موسيقى من مجلد raw", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(rawMusicList) { music ->
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                soundManager.playMusicFromRaw(music.resId)
                                isPlaying = true
                                selectedRawMusic = music.resId
                                selectedUri = null
                            }
                    ) {
                        Text(
                            text = music.title,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}