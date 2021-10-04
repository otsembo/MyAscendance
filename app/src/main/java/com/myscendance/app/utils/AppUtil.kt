package com.myscendance.app.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.util.Log
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.myscendance.app.data.NotificationsReceiver
import com.myscendance.app.workers.NotificationWorker
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.HttpsURLConnection
import kotlin.collections.ArrayList

class AppUtil (private val mCtx:Context) {

    init {
        checkIfFirstInstall()
    }

    private lateinit var calendarArray:ArrayList<Calendar>

    //create single schedule
    private fun createSchedulers(hour:Int) : Calendar {
        //create calendar instance
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar
    }

    //initialize calendars
    private fun initArray(){
        calendarArray = ArrayList()
        for (i in 8 until 17 ){
            calendarArray.add(createSchedulers(i))
        }
    }

    private fun setAlarm(timeMills: Long, mCtx:Context){
        val am = mCtx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mCtx, NotificationsReceiver::class.java)
        val pIntent = PendingIntent.getBroadcast(mCtx, 0, intent, 0)
        am.setRepeating(AlarmManager.RTC, timeMills, AlarmManager.INTERVAL_DAY, pIntent)
    }


    private fun scheduleTasks(){
        //init array
        initArray()
        //use loop to create alarms
        for (x in calendarArray){
            Log.d("TAG", "alarmTime: ${x.time}")
            setAlarm(x.timeInMillis, mCtx)
        }
    }


    private fun checkIfFirstInstall(){
        //shared preferences
        val pref = mCtx.getSharedPreferences(mCtx.applicationContext.packageName, Context.MODE_PRIVATE)
        //check if first install
        val isFirst = pref.getBoolean("${this.javaClass.name}_first", true)
        if(isFirst){
            //schedule the task
            if(canUseWorkManager()) setUpWorker() else  scheduleTasks()

            pref.edit().putBoolean("${this.javaClass.name}_first", false).apply()
        }

    }


    private fun setUpWorker(){

        //add battery constraints
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()


        val notificationsWorker = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(mCtx).enqueue(notificationsWorker)

    }

    private fun checkIfNetworkIsAvailable() : Boolean{
        val connectivityManager = mCtx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo?.isConnectedOrConnecting ?: false

    }

    fun pingNetwork() : Boolean{

        var pinged = false;

        val url = URL("https://www.google.com")

        //create connection object
        val con: HttpsURLConnection?

        //initialize con
        con = url.openConnection() as HttpsURLConnection

        //set connection properties
        con.readTimeout = 10000
        con.connectTimeout = 15000
        con.requestMethod = "GET"
        con.doInput = true


        if(checkIfNetworkIsAvailable()) {
            try {
                con.connect()
                pinged = true
            }catch (e: Exception){
                Log.d("TAG", "pingNetwork: $e")
            }

        }else{
            return false
        }

        return pinged;

    }


    private fun canUseWorkManager() : Boolean{
        return android.os.Build.VERSION.SDK_INT > 22
    }

    fun saveUserCredentials(userId:Int){
        mCtx.getSharedPreferences(this.javaClass.name, Context.MODE_PRIVATE).edit().putInt("USER_ID", userId).apply()
    }

    fun getUserCredential() : Int{
        return mCtx.getSharedPreferences(this.javaClass.name, Context.MODE_PRIVATE).getInt("USER_ID", -1)
    }

    fun isUserLoggedIn() : Boolean{
        return mCtx.getSharedPreferences(this.javaClass.name, Context.MODE_PRIVATE).getBoolean("REMEMBER_USER", false)
    }

    fun stayLoggedIn(userLoggedIn:Boolean){
        mCtx.getSharedPreferences(this.javaClass.name, Context.MODE_PRIVATE).edit().putBoolean("REMEMBER_USER", userLoggedIn).apply()
    }

    fun logOutUser(){
        mCtx.getSharedPreferences(this.javaClass.name, Context.MODE_PRIVATE).edit().putBoolean("REMEMBER_USER", false).apply()
    }


    fun isDarkMode() : Boolean{
        //check if app is in night mode
        return when(mCtx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK){
            Configuration.UI_MODE_NIGHT_YES -> true
            else -> false
        }

    }

}



















