package com.example.mymediaplayerservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class Utility {
    companion object {
        private const val NOTIFICATION_ID = 1
        private var mNotificationManager: NotificationManager? = null

        @RequiresApi(Build.VERSION_CODES.O)
        fun initNotification(songTitle: String, mContext: Context) {
            try {
                val ns = Context.NOTIFICATION_SERVICE
                mNotificationManager =
                    mContext.getSystemService(ns) as NotificationManager
                val icon = R.drawable.img_2
                val tickerText: CharSequence = "Playing your song!"
                val miliTimes: Long = System.currentTimeMillis()

                // Create a notification channel (for Android 8.0 and above)
                val channelId = "my_channel_id"
                val channelName = "My Channel"
                val importance = NotificationManager.IMPORTANCE_LOW
                val channel = NotificationChannel(channelId, channelName, importance)
                mNotificationManager?.createNotificationChannel(channel)

                val notificationBuilder = NotificationCompat.Builder(mContext, channelId)
                    .setSmallIcon(icon)
                    .setContentTitle(songTitle)
                    .setTicker(tickerText)
                    .setWhen(miliTimes)
                    .setContentIntent(getContentIntent(mContext))
                    .setOngoing(true)

                val notification = notificationBuilder.build()
                mNotificationManager?.notify(NOTIFICATION_ID, notification)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun cancelNotification() {
            mNotificationManager?.cancel(NOTIFICATION_ID)
        }

        private fun getContentIntent(mContext: Context): PendingIntent {
            val notificationIntent = Intent(mContext, MainActivity::class.java)
            return PendingIntent.getActivity(
                mContext,
                0,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
            var percentage: Double = 0.0
            var currentSeconds: Long = (currentDuration / 1000).toLong()
            var totalSeconds: Long = (totalDuration / 1000).toLong()

            // Calculating percentage
            percentage = (currentSeconds.toDouble() / totalSeconds) * 100

            // Return percentage
            return percentage.toInt()
        }

        fun progressToTimer(progress: Int, totalDuration: Int): Int {
            var currentDuration = 0
            var total = (totalDuration / 100).toInt()
            currentDuration = ((progress.toDouble() / 100) * total).toInt()

            // Return current duration in milliseconds
            return currentDuration * 1000
        }

        fun milliSecondsToTimer(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString: String

            // Convert total duration into time
            var hours = (milliseconds / (1000 * 60 * 60)).toInt()
            var minutes = ((milliseconds % (1000 * 60 * 60)) / (1000 * 60)).toInt()
            var seconds = (((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000).toInt()

            // Add hours if there
            if (hours > 0) {
                finalTimerString = "$hours:"
            }
            // Prepending 0 to seconds if it is one digit
            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "$seconds"
            }
            finalTimerString = "$finalTimerString$minutes:$secondsString"

            // Return timer string
            return finalTimerString
        }
    }
}
