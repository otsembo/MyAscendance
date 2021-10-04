package com.myscendance.app.network

import com.myscendance.app.data.LoginData
import com.myscendance.app.data.NotificationsData
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    //get all notifications
    @POST("/notifications.php")
    suspend fun getNotifications(@Query("userId") userId:Int) : Response<NotificationsData>

    @POST("/app_login.php")
    suspend fun loginUser(@Query("login_id") login_id:String, @Query("password") password:String) : Response<LoginData>


}