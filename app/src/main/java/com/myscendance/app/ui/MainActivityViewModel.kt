package com.myscendance.app.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.myscendance.app.R
import com.myscendance.app.data.NotificationsData
import com.myscendance.app.data.NotificationsReceiver
import com.myscendance.app.data.SingleNotification
import com.myscendance.app.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel : ViewModel() {

    private val _web_url = "https://app.myascendance.com"
    val web_url:String
        get() = _web_url

    //alert dialog
    fun showNetworkNeededDialog(mCtx: Context){
        //create dialog builder
        val dialogBuilder = MaterialAlertDialogBuilder(mCtx)

        //set properties
        dialogBuilder.setTitle(mCtx.resources.getString(R.string.net_dialog_title))
        dialogBuilder.setMessage(mCtx.resources.getString(R.string.net_dialog_message))
        dialogBuilder.setCancelable(false)
        dialogBuilder.setIcon(R.drawable.ic_baseline_wifi_24)
        dialogBuilder.setPositiveButton("NETWORKS"){
            _,_ -> mCtx.startActivity(Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS))
        }

        dialogBuilder.setNegativeButton("EXIT"){
            _,_ -> (mCtx as AppCompatActivity).finish()
        }

        val dialog = dialogBuilder.create()
        dialog.show()

    }

}