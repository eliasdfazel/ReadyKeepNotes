package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints

class BackgroundSavingWork(appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val databaseEndpoints = DatabaseEndpoints()

    object Foreground {
        const val NotificationId = 123
    }

    private var workerResult: Result = Result.failure()

    override suspend fun doWork(): Result {

        setForegroundAsync(ForegroundInfo(
            BackgroundSavingWork.Foreground.NotificationId,
            NotificationCompat.Builder(applicationContext, this@BackgroundSavingWork.javaClass.simpleName).apply {
                setNotificationSilent()
            }.build()
        ))

        return workerResult
    }

}