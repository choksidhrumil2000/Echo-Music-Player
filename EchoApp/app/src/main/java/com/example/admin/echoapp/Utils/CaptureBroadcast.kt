package com.example.admin.echoapp.Utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.admin.echoapp.Activities.MainActivity
import com.example.admin.echoapp.Fragements.SongPlayingFragement
import com.example.admin.echoapp.R

class CaptureBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent?.action == Intent.ACTION_NEW_OUTGOING_CALL) {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {

                if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {
                    SongPlayingFragement.Statified.mediaPlayer?.pause()
                    SongPlayingFragement.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            try {
                MainActivity.Statified.notificationManager?.cancel(1978)

            } catch (e: Exception) {
                e.printStackTrace()
            }
            val tm: TelephonyManager = context?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tm?.callState) {
                TelephonyManager.CALL_STATE_RINGING -> {

                    try {
                        if (SongPlayingFragement.Statified.mediaPlayer?.isPlaying as Boolean) {
                            SongPlayingFragement.Statified.mediaPlayer?.pause()
                            SongPlayingFragement.Statified.playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                else -> {

                }
            }

        }

    }

}