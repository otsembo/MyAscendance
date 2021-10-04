package com.myscendance.app.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.myscendance.app.data.NotificationsManager

class NotificationWorker(private val mCtx:Context, workerParameters: WorkerParameters) : Worker(mCtx, workerParameters) {

    override fun doWork(): Result {
        //set up notifications
        NotificationsManager(mCtx)

        return Result.success()
    }

}