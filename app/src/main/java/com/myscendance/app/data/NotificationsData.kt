package com.myscendance.app.data

import com.google.gson.annotations.Expose
import java.io.Serializable

data class NotificationsData(

    val items: Int,

    val message: String,

    val notifications: List<SingleNotification>

) : Serializable
