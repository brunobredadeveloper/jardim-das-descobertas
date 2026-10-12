package dev.breda.garden

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.*

/** Adapter uses only a device voice that does not require a network connection. */
class SpeechGuide(context: Context) : AutoCloseable {
    var available by mutableStateOf(false)
        private set

    private var engine: TextToSpeech? = null
    private var requested: String? = null

    init {
        engine =
            TextToSpeech(context.applicationContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val voice =
                        engine
                            ?.voices
                            ?.filter {
                                it.locale.language == "pt" && !it.isNetworkConnectionRequired
                            }
                            ?.sortedByDescending { it.locale.country == "BR" }
                            ?.firstOrNull()
                    if (voice != null) {
                        engine?.voice = voice
                        engine?.setSpeechRate(.83f)
                        engine?.setPitch(1.12f)
                        available = true
                        requested?.let { speak(it) }
                    }
                }
            }
    }

    fun speak(text: String) {
        requested = text
        if (available)
            engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "garden-${System.nanoTime()}")
    }

    fun stop() {
        requested = null
        engine?.stop()
    }

    override fun close() {
        stop()
        engine?.shutdown()
        engine = null
    }
}
