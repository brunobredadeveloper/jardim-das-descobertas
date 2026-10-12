package dev.breda.garden

import android.media.MediaPlayer
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test

class AnimalRecordingTest {
    @Test
    fun allThreeBundledRecordingsDecodeAndCompleteOffline() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        for (resource in listOf(R.raw.animal_cat, R.raw.animal_dog, R.raw.animal_cow)) {
            val player = MediaPlayer()
            val completed = CountDownLatch(1)
            var failed = false
            try {
                context.resources.openRawResourceFd(resource).use {
                    player.setDataSource(it.fileDescriptor, it.startOffset, it.length)
                }
                player.setOnCompletionListener { completed.countDown() }
                player.setOnErrorListener { _, _, _ ->
                    failed = true
                    completed.countDown()
                    true
                }
                player.prepare()
                assertTrue(player.duration > 500)
                player.setVolume(0f, 0f)
                player.start()
                assertTrue("Recording did not complete", completed.await(15, TimeUnit.SECONDS))
                assertFalse("Recording failed decoding", failed)
            } finally {
                player.release()
            }
        }
    }
}
