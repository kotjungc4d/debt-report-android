package com.fintech.debtreport.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.view.HapticFeedbackConstants
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.PI
import kotlin.math.sin

/**
 * UiSoundEffects.kt
 * PCM Real-time Synthesizer สำหรับสัมผัสเสียงคลีนหรูสไตล์ FinTech
 * ไม่พึ่งพาไฟล์ MP3/OGG ภายนอก สังเคราะห์คลื่น Sine/Harmonic 16-bit PCM แบบ Low-latency
 */
object UiSoundEffects {
    private const val SAMPLE_RATE = 44100
    private val audioScope = CoroutineScope(Dispatchers.Default)
    var isSoundEnabled: Boolean = true

    fun playClick(view: View? = null) {
        view?.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        if (!isSoundEnabled) return
        audioScope.launch {
            synthesizeTone(startFreq = 920.0, endFreq = 340.0, durationMs = 32, amplitude = 0.22)
        }
    }

    fun playTabSwitch(view: View? = null) {
        view?.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
        if (!isSoundEnabled) return
        audioScope.launch {
            synthesizeTone(startFreq = 520.0, endFreq = 780.0, durationMs = 55, amplitude = 0.18)
        }
    }

    fun playCheckSuccess(view: View? = null) {
        view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        if (!isSoundEnabled) return
        audioScope.launch {
            synthesizeChord(
                freqs = doubleArrayOf(587.33, 880.0), // D5 -> A5 Luxury Sage Fifth
                stepDelayMs = 55,
                durationMs = 150,
                amplitude = 0.25
            )
        }
    }

    fun playCashflowSaved(isIncome: Boolean, view: View? = null) {
        view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        if (!isSoundEnabled) return
        audioScope.launch {
            val notes = if (isIncome) {
                doubleArrayOf(523.25, 659.25, 1046.50)
            } else {
                doubleArrayOf(659.25, 523.25)
            }
            synthesizeChord(freqs = notes, stepDelayMs = 45, durationMs = 130, amplitude = 0.24)
        }
    }

    private fun synthesizeTone(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        amplitude: Double
    ) {
        val numSamples = (SAMPLE_RATE * durationMs) / 1000
        val pcmBuffer = ShortArray(numSamples)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = startFreq + (endFreq - startFreq) * progress
            val envelope = exp(-5.5 * progress) // Exponential decay
            phase += (2.0 * PI * freq) / SAMPLE_RATE
            val sample = (sin(phase) * amplitude * envelope * Short.MAX_VALUE).toInt()
            pcmBuffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        writePcmToTrack(pcmBuffer)
    }

    private fun synthesizeChord(
        freqs: DoubleArray,
        stepDelayMs: Int,
        durationMs: Int,
        amplitude: Double
    ) {
        val totalDurationMs = durationMs + (freqs.size - 1) * stepDelayMs
        val numSamples = (SAMPLE_RATE * totalDurationMs) / 1000
        val pcmBuffer = ShortArray(numSamples)

        for ((index, freq) in freqs.withIndex()) {
            val startSample = (SAMPLE_RATE * (index * stepDelayMs)) / 1000
            val noteSamples = (SAMPLE_RATE * durationMs) / 1000
            var phase = 0.0
            for (i in 0 until noteSamples) {
                val targetIdx = startSample + i
                if (targetIdx >= numSamples) break
                val progress = i.toDouble() / noteSamples
                val envelope = exp(-4.8 * progress)
                phase += (2.0 * PI * freq) / SAMPLE_RATE
                val added = (sin(phase) * (amplitude / freqs.size) * envelope * Short.MAX_VALUE).toInt()
                val mixed = pcmBuffer[targetIdx].toInt() + added
                pcmBuffer[targetIdx] = mixed.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        writePcmToTrack(pcmBuffer)
    }

    private fun writePcmToTrack(pcmBuffer: ShortArray) {
        val byteSize = pcmBuffer.size * 2
        val track = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(SAMPLE_RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(byteSize.coerceAtLeast(AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcmBuffer, 0, pcmBuffer.size)
        track.play()
    }
}