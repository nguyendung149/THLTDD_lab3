package com.example.mymediaplayerservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var btnPlay: ImageButton
        lateinit var btnStop:ImageButton
        lateinit var elapsedTime : TextView
        lateinit var remainingTime : TextView
        lateinit var seekBar:SeekBar
        lateinit var btnLyric:ImageButton
        lateinit var lyricView: TextView
    }
    lateinit var playerServer:Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        playerServer = Intent(this@MainActivity,PlayerInService::class.java)
        startService(playerServer)

    }
    private fun initView(){
        btnPlay = findViewById(R.id.btnPlay)
        btnStop = findViewById(R.id.btnStop)
        seekBar = findViewById(R.id.seekBar)
        elapsedTime = findViewById(R.id.elapsedTime)
        remainingTime = findViewById(R.id.remainingTime)
        lyricView = findViewById(R.id.textViewLyric)
        btnLyric = findViewById(R.id.btnViewLyrics)
        var isLyricsVisible = true
        val lyricsText = readLyricsFromFile()
        lyricView.text = lyricsText
        btnLyric.setOnClickListener {

            isLyricsVisible = !isLyricsVisible

            if (isLyricsVisible) {
                lyricView.visibility = View.VISIBLE
            } else {
                lyricView.visibility = View.INVISIBLE
            }
        }

    }
    fun readLyricsFromFile(): String {
        val inputStream = resources.openRawResource(R.raw.lyrics)
        return inputStream.bufferedReader().use { it.readText() }
    }
    override fun onDestroy() {
        if(!PlayerInService.mp.isPlaying){
            PlayerInService.mp.stop()
            stopService(playerServer)

        }else{
            btnPlay.setBackgroundResource(R.drawable.imagebuttonpause)
        }
        Utility.cancelNotification()
        super.onDestroy()
    }
}