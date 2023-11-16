package com.example.mymediaplayerservice

import android.app.Service
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import java.lang.ref.WeakReference

class PlayerInService():Service(),OnClickListener,MediaPlayer.OnCompletionListener,SeekBar.OnSeekBarChangeListener {
    lateinit var btnPlay:WeakReference<ImageButton>
    lateinit var btnStop:WeakReference<ImageButton>
    lateinit var textViewSongTime:WeakReference<TextView>
    lateinit var songProgressBar:WeakReference<SeekBar>
    var progressBarHandler = Handler()
    var isPause:Boolean = false
    companion object{
        lateinit var mp:MediaPlayer
    }

    override fun onCreate() {
        mp = MediaPlayer()
        mp.reset()
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initUI();
        super.onStart(intent,startId)
        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    private fun initUI(){
        btnPlay = WeakReference<ImageButton>(MainActivity.btnPlay)
        btnStop = WeakReference<ImageButton>(MainActivity.btnStop)
        textViewSongTime = WeakReference<TextView>(MainActivity.textViewSongTime)
        songProgressBar = WeakReference<SeekBar>(MainActivity.seekBar)
        songProgressBar.get()?.setOnClickListener(this)
        btnPlay.get()?.setOnClickListener(this)
        btnStop.get()?.setOnClickListener(this)
        mp.setOnCompletionListener(this)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnPlay -> {
                if(mp.isPlaying){
                    mp.pause()
                    isPause =true
                    progressBarHandler.removeCallbacks(mUpdateTimeTask)
                    btnPlay.get()?.setBackgroundResource(R.drawable.imagebuttonplay)
                    return
                }
                if(isPause){
                    mp.start()
                    isPause = false
                    updateProgressBar()
                    btnPlay.get()?.setBackgroundResource(R.drawable.imagebuttonpause)
                    return
                }
                if(!mp.isPlaying){
                    playSong()
                }

            }
            R.id.btnStop -> {
                mp.stop()
                onCompletion(mp)
                textViewSongTime.get()?.text = "0.00/0.00"
            }
        }
    }
    fun updateProgressBar(){
        try {
            progressBarHandler.postDelayed(mUpdateTimeTask,100)
            Log.i("MMM",mUpdateTimeTask.toString())
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    private val mUpdateTimeTask:Runnable = object: Runnable{
        override fun run(){
            var totalDuration:Long = 0
            var currentDuration:Long = 0
            try {
                totalDuration = mp.duration.toLong()
                currentDuration = mp.currentPosition.toLong()
                textViewSongTime.get()?.text = "${Utility.milliSecondsToTimer(currentDuration)}/${Utility.milliSecondsToTimer(totalDuration)}"
                var progress = Utility.getProgressPercentage(currentDuration,totalDuration).toInt()
                Log.d("DUNG",progress.toString())
                songProgressBar.get()?.progress = progress //Running this thread after 100 millision
                progressBarHandler.postDelayed(this,100)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
    override fun onCompletion(p0: MediaPlayer?) {
        songProgressBar.get()?.progress = 0
        progressBarHandler.removeCallbacks(mUpdateTimeTask)
        btnPlay.get()?.setBackgroundResource(R.drawable.imagebuttonplay)
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {
      progressBarHandler.removeCallbacks(mUpdateTimeTask)
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        progressBarHandler.removeCallbacks(mUpdateTimeTask)
        var totalDuration = mp.duration
        var currentPosition = Utility.progressToTimer(p0!!.progress,totalDuration)
        mp.seekTo(currentPosition)
        updateProgressBar()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
    //Play song
    @RequiresApi(Build.VERSION_CODES.O)
    fun playSong(){
        Utility.initNotification("Playing (Cháu lên ba)...",this)
        try {
            mp.reset()
            var myUri:Uri = Uri.parse("android.resource://${this.packageName}/${R.raw.chaulenba}")
            mp.setDataSource(this,myUri)
            mp.prepareAsync()
            mp.setOnPreparedListener {
                try {
                    mp.start()
                    updateProgressBar()
                    btnPlay.get()?.setBackgroundResource(R.drawable.imagebuttonpause)
                }catch (e:Exception){
                    e.printStackTrace()
                    Log.i("EXCEPTION","${e.message}")
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}