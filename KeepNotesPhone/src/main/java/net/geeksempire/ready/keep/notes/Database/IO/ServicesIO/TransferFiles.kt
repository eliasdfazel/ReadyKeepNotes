package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Notes.Tools.BaseDirectory
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.NotificationBuilder
import java.io.File

class TransferFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    var totalProcess = 0
    var allProcessCounter = 0

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
                    putExtra(TransferFiles.BaseDirectory, baseDirectory)
                })

            } else {

                context.startService(Intent(context, TransferFiles::class.java).apply {
                    putExtra(TransferFiles.BaseDirectory, baseDirectory)
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

                if (inputData.hasExtra(TransferFiles.BaseDirectory)) {

                    startForeground(TransferFiles.Foreground.NotificationId,
                        NotificationBuilder(applicationContext)
                            .create(notificationId = TransferFiles.Foreground.NotificationId, notificationChannelId = this@TransferFiles.javaClass.simpleName,
                                notificationTitle = getString(R.string.applicationName), notificationContent = getString(R.string.transferFilesText)))

                    val firebaseStorage = Firebase.storage

                    val baseDirectory: String = inputData.getStringExtra(TransferFiles.BaseDirectory)!!

                    // Send Handwriting Snapshot
                    val allFilesDirectory = File(BaseDirectory().localBaseDirectory(baseDirectory, firebaseUser.uid))

                    if (allFilesDirectory.exists()) {

                        allFilesDirectory.listFiles()?.forEach { eachNoteDirectory ->

                            val uniqueDocumentId = eachNoteDirectory.name

                            eachNoteDirectory.listFiles()?.forEach { eachDirectory ->

                                if (eachDirectory.name == LocalDirectories.HandwritingSnapshotLocalFile) {// Get Handwriting Snapshot

                                    totalProcess++

                                    val handwritingSnapshot = eachDirectory.listFiles()?.get(0)

                                    if (handwritingSnapshot != null
                                        && handwritingSnapshot.exists()) {

                                        firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$uniqueDocumentId.PNG"))
                                            .putBytes(handwritingSnapshot.readBytes())
                                            .addOnSuccessListener {

                                                allProcessCounter++

                                            }

                                    }

                                } else if (eachDirectory.name == LocalDirectories.AudioRecordingLocalFile) {// Get Audio Recording

                                    totalProcess.plus(eachDirectory.listFiles()?.size?:0)

                                    eachDirectory.listFiles()?.forEach { audioRecordingDirectory ->

                                        val audioRecordingFileId = audioRecordingDirectory.name

                                        if (audioRecordingDirectory.exists()) {

                                            firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$audioRecordingFileId"))
                                                .putBytes(audioRecordingDirectory.readBytes())
                                                .addOnSuccessListener {

                                                    allProcessCounter++

                                                }

                                        }

                                    }

                                } else if (eachDirectory.name == LocalDirectories.ImagingLocalFile) {// Get Imaging

                                    totalProcess.plus(eachDirectory.listFiles()?.size?:0)

                                    eachDirectory.listFiles()?.forEach { imagingDirectory ->

                                        val imagingFileId = imagingDirectory.name

                                        if (imagingDirectory.exists()) {

                                            firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$imagingFileId.PNG"))
                                                .putBytes(imagingDirectory.readBytes())
                                                .addOnSuccessListener {

                                                    allProcessCounter++

                                                }

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

    private fun allProcessFinished(totalProcess: Int, allProcessCounter: Int) : Boolean {

        return if (allProcessCounter == totalProcess) {

            stopForeground(true)

            true
        } else {



            false
        }
    }

}