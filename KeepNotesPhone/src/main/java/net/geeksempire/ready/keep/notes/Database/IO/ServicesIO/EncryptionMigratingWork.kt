package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
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

            val newTitle = noteTitle?.let { title -> contentEncryption.encryptEncodedData(title, newEncryptionPassword)?.asList().toString() }
            val newContent = noteTextContent?.let { content -> contentEncryption.encryptEncodedData(content, newEncryptionPassword)?.asList().toString() }

            notesDatabaseDataAccessObject.updateCompleteNoteData(NotesDatabaseModel(
                it.uniqueNoteId,
                newTitle,
                newContent,
                it.noteHandwritingPaintingPaths,
                it.noteHandwritingSnapshotLink,
                it.noteVoicePaths,
                it.noteImagePaths,
                it.noteGifPaths,
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

        delay(3333)

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
        notificationBuilder.setNotificationSilent()

        if (notificationDone) {

            notificationBuilder.setContentText(applicationContext.getString(R.string.doneText))
            notificationBuilder.setAutoCancel(true)
            notificationBuilder.setOngoing(false)

            notificationManager.notify(EncryptionMigratingWork.Foreground.NotificationId, notificationBuilder.build())

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            notificationBuilder.setChannelId(applicationContext.packageName)

            val notificationChannel = NotificationChannel(
                applicationContext.packageName,
                applicationContext.getString(R.string.applicationName),
                NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(notificationChannel)

        }

        return notificationBuilder.build()
    }

}