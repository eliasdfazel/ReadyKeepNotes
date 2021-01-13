package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.*
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformationIO
import net.geeksempire.ready.keep.notes.Database.Configurations.Offline.NotesRoomDatabaseConfiguration
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption

class EncryptionMigratingWork(val appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    val contentEncryption = ContentEncryption()

    object Foreground {
        const val NotificationId = 123
    }

    private var workerResult: Result = Result.failure()

    override suspend fun doWork(): Result {

        setForegroundAsync(ForegroundInfo(
            EncryptionMigratingWork.Foreground.NotificationId,
            createNotification(false)
        ))

        val userInformationIO = UserInformationIO(applicationContext)

        val oldEncryptionPassword = userInformationIO.getOldFirebaseUniqueIdentifier()
        val newEncryptionPassword = userInformationIO.getNewFirebaseUniqueIdentifier()

        val notesRoomDatabaseConfiguration: NotesRoomDatabaseConfiguration = NotesRoomDatabaseConfiguration(applicationContext)

        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration.prepareRead()

        val allOldEncryptedData = notesDatabaseDataAccessObject.getAllNotesData()

        allOldEncryptedData.forEach {

            val noteTitle = it.noteTile?.let { title -> contentEncryption.decryptEncodedData(title, oldEncryptionPassword) }
            val noteTextContent = it.noteTextContent?.let { content -> contentEncryption.decryptEncodedData(content, oldEncryptionPassword) }

            println(">>> >> > " + noteTextContent)

            val newTitle = noteTitle?.let { title -> contentEncryption.encryptEncodedData(title, newEncryptionPassword).asList().toString() }
            val newContent = noteTextContent?.let { content -> contentEncryption.encryptEncodedData(content, newEncryptionPassword).asList().toString() }

            notesDatabaseDataAccessObject.updateNoteData(NotesDatabaseModel(
                it.uniqueNoteId,
                newTitle,
                newContent,
                it.noteHandwritingPaintingPaths,
                it.noteHandwritingSnapshotLink,
                it.noteVoiceContent,
                it.noteImageContent,
                it.noteGifContent,
                it.noteTakenTime,
                it.noteEditTime,
                it.noteIndex,
                it.noteTags,
                it.noteHashTags,
                it.noteTranscribeTags,
                it.dataSelected
            ))

        }

        notesRoomDatabaseConfiguration.closeDatabase()

        createNotification(true)

        workerResult = Result.success()

        return workerResult
    }

    private fun createNotification(notificationDone: Boolean) : Notification {

        val notificationManager = applicationContext.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder = NotificationCompat.Builder(applicationContext, this@EncryptionMigratingWork.javaClass.simpleName)
        notificationBuilder.setContentTitle(applicationContext.getString(R.string.applicationName))
        notificationBuilder.setContentText(applicationContext.getString(R.string.migratingDatabaseEncryption))
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
        notificationBuilder.color = applicationContext.getColor(R.color.default_color)

        if (notificationDone) {

            notificationBuilder.setContentText(applicationContext.getString(R.string.doneText))
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setOngoing(false)

            Handler(Looper.getMainLooper()).postDelayed({

                notificationManager.cancelAll()

            }, 555)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                applicationContext.packageName,
                applicationContext.getString(R.string.applicationName),
                NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setChannelId(applicationContext.packageName)

        }

        return notificationBuilder.build()
    }

}