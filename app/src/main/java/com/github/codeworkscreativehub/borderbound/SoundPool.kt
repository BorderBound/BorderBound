package com.github.codeworkscreativehub.borderbound

import android.app.Activity
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.SparseIntArray

class SoundPool internal constructor(a: Activity) {
    private val pool: SoundPool
    private val items = SparseIntArray()
    private val myContext: Context

    init {
        a.volumeControlStream = AudioManager.STREAM_MUSIC
        myContext = a

        val builder = SoundPool.Builder()
        builder.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        builder.setMaxStreams(3)
        pool = builder.build()

        pool.setOnLoadCompleteListener { _, _, _ -> }
    }

    fun loadSound(resID: Int) {
        items.put(resID, pool.load(myContext, resID, 1))
    }

    fun playSound(resID: Int) {
        val audioManager = myContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val actualVolume = audioManager
            .getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        val maxVolume = audioManager
            .getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val volume = actualVolume / maxVolume

        pool.play(items.get(resID), volume, volume, 1, 0, 1f)
    }
}