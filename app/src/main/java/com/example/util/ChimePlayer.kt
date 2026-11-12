package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class ChimePlayer(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.getDefault())
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            tts?.setPitch(1.05f)
            tts?.setSpeechRate(0.92f) // slightly calm, deliberate cadence
        }
    }

    fun speakAffirmation(text: String) {
        if (isTtsReady && text.isNotBlank()) {
            tts?.stop()
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "369_affirmation_speech")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    /**
     * Synthesizes a pure celestial bell chime (fundamental Solfeggio frequency + gentle harmonics)
     * with smooth exponential decay. No external audio file needed.
     */
    fun playChime(frequencyHz: Float = 528f, scope: CoroutineScope) {
        scope.launch(Dispatchers.Default) {
            try {
                val sampleRate = 44100
                val durationSeconds = 1.2f
                val numSamples = (durationSeconds * sampleRate).toInt()
                val buffer = ShortArray(numSamples)

                val decayRate = 3.5 // Smooth bell exponential decay
                val harmonic2 = frequencyHz * 2f
                val harmonic3 = frequencyHz * 3f

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    val envelope = exp(-decayRate * time)

                    // Fundamental + subtle harmonics for rich acoustic singing-bowl resonance
                    val wave = (
                        0.75 * sin(2.0 * PI * frequencyHz * time) +
                        0.20 * sin(2.0 * PI * harmonic2 * time) +
                        0.05 * sin(2.0 * PI * harmonic3 * time)
                    ) * envelope

                    val sampleValue = (wave * Short.MAX_VALUE * 0.85).toInt()
                    buffer[i] = sampleValue.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                // Release track after playback completes
                launch(Dispatchers.IO) {
                    Thread.sleep((durationSeconds * 1000).toLong() + 100)
                    try {
                        audioTrack.stop()
                        audioTrack.release()
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {
                // Audio fallback gracefully handled
            }
        }
    }

    fun playHapticFeedback() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createOneShot(45, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(45)
                }
            }
        } catch (_: Exception) {}
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
