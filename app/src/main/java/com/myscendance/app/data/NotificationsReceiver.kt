package com.myscendance.app.data

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.myscendance.app.R
import com.myscendance.app.network.ApiClient
import com.myscendance.app.ui.MainActivity
import com.myscendance.app.utils.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {

        Log.d("TAG", "onReceive: broadcast")

        p0?.let {
            NotificationsManager(it)
        }

    }



}