package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints

class BackgroundSavingWork(appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val databaseEndpoints = DatabaseEndpoints()

    private var workerResult: Result = Result.failure()

    override suspend fun doWork(): Result {

        setForegroundAsync(ForegroundInfo())

        return workerResult
    }

}