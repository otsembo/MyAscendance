package com.myscendance.app.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.myscendance.app.R
import com.myscendance.app.network.ApiClient
import com.myscendance.app.ui.MainActivity
import com.myscendance.app.utils.AppUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationsManager(private val mCtx: Context) {

    private val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    private lateinit var  appUtil:AppUtil

    init {

        var validConnection: Boolean

        appUtil = AppUtil(mCtx)

        CoroutineScope(Dispatchers.IO).launch {
            validConnection = appUtil.pingNetwork()
            //if valid connection
            if(validConnection) getNotifications()
        }

    }


    private suspend fun getNotifications(){

        val notifyData = ApiClient.apiService.getNotifications(appUtil.getUserCredential())

        if(notifyData.isSuccessful){
            val body = notifyData.body()
            body?.let {
                val singleData = it.notifications
                for((channelRequestCode, x) in singleData.withIndex()){
                    //build individual notifications
                    buildNotifications(x, channelRequestCode)
                }

            }
        }

    }


    private fun buildNotifications(singleNotification: SingleNotification, code:Int){
        //intent
        val intent = Intent(mCtx, MainActivity::class.java).putExtra("action", singleNotification.Action)

        val pIntent = PendingIntent.getActivity(mCtx, code, intent, 0)

        val manager = mCtx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(mCtx, default_notification_channel_id).
        setSmallIcon(R.mipmap.ic_launcher_round).
        setContentTitle(mCtx.resources.getString(R.string.app_name)).
        setContentText(singleNotification.Description).
        setContentIntent(pIntent).
        setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME",
                importance
            )
            builder.setChannelId(NOTIFICATION_CHANNEL_ID)
            manager.createNotificationChannel(notificationChannel)
        }

        manager.notify(code, builder.build())

    }

}