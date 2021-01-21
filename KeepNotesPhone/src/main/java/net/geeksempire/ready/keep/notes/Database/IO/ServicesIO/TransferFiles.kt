package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording.AudioRecordingLocalFile
import net.geeksempire.ready.keep.notes.Notes.Tools.BaseDirectory
import net.geeksempire.ready.keep.notes.Notes.Tools.Imaging.ImagingLocalFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.HandwritingSnapshotLocalFile
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.NotificationBuilder
import java.io.File

class TransferFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    object Foreground {
        const val NotificationId = 357
    }

    object LocalDirectories {
        const val HandwritingSnapshotLocalFile = "HandwritingSnapshot"
        const val AudioRecordingLocalFile = "AudioRecording"
        const val ImagingLocalFile = "Imaging"
    }

    companion object {
        const val BaseDirectory = "BaseDirectory"

        /**
         * @param baseDirectory = externalMediaDirs[0].path
         **/
        fun startProcess(context: Context,
                         baseDirectory: String) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                context.startForegroundService(Intent(context, TransferFiles::class.java).apply {
                    putExtra(RetrieveFiles.BaseDirectory, baseDirectory)
                })

            } else {

                context.startService(Intent(context, RetrieveFiles::class.java).apply {
                    putExtra(RetrieveFiles.BaseDirectory, baseDirectory)
                })

            }

        }

    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Firebase.auth.currentUser?.let { firebaseUser ->

            intent?.let { inputData ->

                if (inputData.hasExtra(RetrieveFiles.BaseDirectory)) {

                    startForeground(RetrieveFiles.Foreground.NotificationId,
                        NotificationBuilder(applicationContext)
                            .create(notificationId = TransferFiles.Foreground.NotificationId, notificationChannelId = this@TransferFiles.javaClass.simpleName,
                                notificationTitle = getString(R.string.applicationName), notificationContent = getString(R.string.transferFilesText)))

                    val firebaseStorage = Firebase.storage

                    val baseDirectory: String = inputData.getStringExtra(RetrieveFiles.BaseDirectory)!!

                    val handwritingSnapshotLocalFile = HandwritingSnapshotLocalFile(baseDirectory)

                    val audioRecordingFile = AudioRecordingLocalFile(baseDirectory)

                    val imagingFile = ImagingLocalFile(baseDirectory)

                    // Send Handwriting Snapshot
                    val allFilesDirectory = File(BaseDirectory().localBaseDirectory(baseDirectory, firebaseUser.uid))

                    if (allFilesDirectory.exists()) {

                        allFilesDirectory.listFiles()?.forEach { eachDirectory ->

                            if (eachDirectory.name == LocalDirectories.HandwritingSnapshotLocalFile) {

                                val handwritingSnapshot = eachDirectory.listFiles()?.get(0)

                                if (handwritingSnapshot != null
                                    && handwritingSnapshot.exists()) {

                                    val uniqueDocumentId = handwritingSnapshot.name.replace(".PNG", "")

                                    firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$uniqueDocumentId.PNG"))
                                        .putBytes(handwritingSnapshot.readBytes())

                                }

                            } else if (eachDirectory.name == LocalDirectories.AudioRecordingLocalFile) {

                                eachDirectory.listFiles()?.forEach { audioRecordingDirectory ->

                                    val uniqueDocumentId = audioRecordingDirectory.name

                                    audioRecordingDirectory.listFiles()?.forEach { audioRecordingFile ->

                                        if (audioRecordingFile.exists()) {

                                            firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$uniqueDocumentId.MP3"))
                                                .putBytes(audioRecordingFile.readBytes())

                                        }

                                    }

                                }

                            } else if (eachDirectory.name == LocalDirectories.ImagingLocalFile) {

                                eachDirectory.listFiles()?.forEach { imagingDirectory ->

                                    val uniqueDocumentId = imagingDirectory.name

                                    imagingDirectory.listFiles()?.forEach { imagingFile ->

                                        if (imagingFile.exists()) {

                                            firebaseStorage.getReference(databaseEndpoints.imageEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$uniqueDocumentId.PNG"))
                                                .putBytes(imagingFile.readBytes())

                                        }

                                    }

                                }

                            }

                        }

                    }


                }

            }

        }


        return Service.START_STICKY
    }

}