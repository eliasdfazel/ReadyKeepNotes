package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformationIO
import net.geeksempire.ready.keep.notes.Database.Configurations.Offline.NotesRoomDatabaseConfiguration

class EncryptionMigratingWork(val appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    object Foreground {
        const val NotificationId = 123
    }

    private var workerResult: Result = Result.failure()

    override suspend fun doWork(): Result {

        setForegroundAsync(ForegroundInfo(
            EncryptionMigratingWork.Foreground.NotificationId,
            NotificationCompat.Builder(applicationContext, this@EncryptionMigratingWork.javaClass.simpleName).apply {
                setNotificationSilent()
            }.build()
        ))

        val userInformationIO = UserInformationIO(applicationContext)



        val notesRoomDatabaseConfiguration: NotesRoomDatabaseConfiguration = NotesRoomDatabaseConfiguration(applicationContext)



        workerResult = Result.success()

        return workerResult
    }

}