package net.alkafeel.questions

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import net.alkafeel.questions.R

class SoundManager(private val context: Context) {
    private val soundPool: SoundPool
    private val soundCorrect: Int
    private val soundIncorrect: Int
    private var mediaPlayer: MediaPlayer? = null

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(2)
            .setAudioAttributes(audioAttributes)
            .build()
        // تأكد أن الملفات موجودة في res/raw
        soundCorrect = soundPool.load(context, R.raw.correct, 1)
        soundIncorrect = soundPool.load(context, R.raw.lose, 1)
    }

    fun playCorrect() {
        soundPool.play(soundCorrect, 1f, 1f, 1, 0, 1f)
    }

    fun playIncorrect() {
        soundPool.play(soundIncorrect, 1f, 1f, 1, 0, 1f)
    }

    fun playMusicFromUri(uri: android.net.Uri) {
        stopMusic() // إيقاف الموسيقى الحالية إن وُجدت
        mediaPlayer = MediaPlayer.create(context, uri)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun playMusicFromRaw(resId: Int) {
        stopMusic() // إيقاف الموسيقى الحالية إن وُجدت
        mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
