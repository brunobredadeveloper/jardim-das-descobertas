package dev.breda.garden

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.compose.runtime.*
import dev.breda.garden.domain.*

/** Bundled recordings: one player, no network, released on every route/lifecycle change. */
class AnimalAudioPlayer(context: Context) : AutoCloseable {
    private val context = context.applicationContext
    private val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var player: MediaPlayer? = null
    var status by mutableStateOf(AudioStatus.IDLE)
        private set

    private val attributes =
        AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
    private val focus =
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
            .setAudioAttributes(attributes)
            .setOnAudioFocusChangeListener { change ->
                if (
                    change == AudioManager.AUDIOFOCUS_LOSS ||
                        change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT
                )
                    stop()
            }
            .build()

    fun play(token: Token) {
        stop()
        val resource =
            when (token) {
                Token.CAT -> R.raw.animal_cat
                Token.DOG -> R.raw.animal_dog
                Token.COW -> R.raw.animal_cow
                else -> error("No animal recording")
            }
        if (manager.requestAudioFocus(focus) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            status = AudioStatus.ERROR
            return
        }
        status = AudioStatus.LOADING
        val current = MediaPlayer()
        player = current
        runCatching {
                current.setAudioAttributes(attributes)
                current.setVolume(.3f, .3f)
                context.resources.openRawResourceFd(resource).use {
                    current.setDataSource(it.fileDescriptor, it.startOffset, it.length)
                }
                current.setOnPreparedListener { ready ->
                    if (player === ready) {
                        status = AudioStatus.PLAYING
                        ready.start()
                    }
                }
                current.setOnCompletionListener { done -> if (player === done) stop() }
                current.setOnErrorListener { failed, _, _ ->
                    if (player === failed) {
                        stop()
                        status = AudioStatus.ERROR
                    }
                    true
                }
                current.prepareAsync()
            }
            .onFailure {
                stop()
                status = AudioStatus.ERROR
            }
    }

    fun stop() {
        player?.let {
            it.setOnPreparedListener(null)
            it.setOnCompletionListener(null)
            it.setOnErrorListener(null)
            it.release()
        }
        player = null
        manager.abandonAudioFocusRequest(focus)
        status = AudioStatus.IDLE
    }

    override fun close() = stop()
}

enum class AudioStatus {
    IDLE,
    LOADING,
    PLAYING,
    ERROR,
}
