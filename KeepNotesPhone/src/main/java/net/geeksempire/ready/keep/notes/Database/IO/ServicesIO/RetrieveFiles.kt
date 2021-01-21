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
import net.geeksempire.ready.keep.notes.Notes.Tools.Imaging.ImagingLocalFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.HandwritingSnapshotLocalFile
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Extensions.println
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.NotificationBuilder
import java.io.File

class RetrieveFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    object Foreground {
        const val NotificationId = 357
    }

    companion object {
        const val BaseDirectory = "BaseDirectory"

        /**
         * @param baseDirectory = externalMediaDirs[0].path
         **/
        fun startProcess(context: Context,
                         baseDirectory: String) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                context.startForegroundService(Intent(context, RetrieveFiles::class.java).apply {
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
                            .create(notificationId = RetrieveFiles.Foreground.NotificationId, notificationChannelId = this@RetrieveFiles.javaClass.simpleName,
                                notificationTitle = getString(R.string.applicationName), notificationContent = getString(R.string.retrievingFilesText)))

                    val firebaseStorage = Firebase.storage

                    val baseDirectory: String = inputData.getStringExtra(RetrieveFiles.BaseDirectory)!!

                    val handwritingSnapshotLocalFile = HandwritingSnapshotLocalFile(baseDirectory)

                    val audioRecordingFile = AudioRecordingLocalFile(baseDirectory)

                    val imagingFile = ImagingLocalFile(baseDirectory)

                    firebaseStorage.getReference(databaseEndpoints.generalEndpoints(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener { allNotesReference ->

                            // All Directories -> Document Unique Identifier
                            allNotesReference.prefixes.forEach { aNoteReference ->

                                val uniqueDocumentId = aNoteReference.name

                                // Get Handwriting Snapshot
                                val handwritingSnapshotDirectoryPath = File(handwritingSnapshotLocalFile.getHandwritingSnapshotDirectoryPath(firebaseUser.uid, uniqueDocumentId))

                                if (!handwritingSnapshotDirectoryPath.exists()) {

                                    handwritingSnapshotDirectoryPath.mkdirs()

                                }

                                firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid, uniqueDocumentId).plus("/$uniqueDocumentId.PNG"))
                                    .getFile(File(handwritingSnapshotLocalFile.getHandwritingSnapshotFilePath(firebaseUser.uid, uniqueDocumentId)))
                                    .addOnCompleteListener {


                                    }


                                // Get Audio Recording
                                val audioRecordingDirectoryPath = File(audioRecordingFile.getAudioRecordingDirectoryPath(firebaseUser.uid, uniqueDocumentId))

                                if (!audioRecordingDirectoryPath.exists()) {

                                    audioRecordingDirectoryPath.mkdirs()

                                }

                                databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, uniqueDocumentId).println()
                                firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid, uniqueDocumentId))
                                    .listAll()
                                    .addOnSuccessListener { voiceRecordingListResults ->

                                        voiceRecordingListResults.items.forEach { storageReferenceAudioFiles ->

                                            storageReferenceAudioFiles.println()

                                            val audioFileId = storageReferenceAudioFiles.name

                                            storageReferenceAudioFiles
                                                .getFile(File(audioRecordingFile.getAudioRecordingFilePath(firebaseUser.uid, uniqueDocumentId, audioFileId)))

                                        }

                                    }


                                // Get Imaging
                                val imagingDirectoryPath = File(imagingFile.getImagingDirectoryPath(firebaseUser.uid, uniqueDocumentId))

                                if (!imagingDirectoryPath.exists()) {

                                    imagingDirectoryPath.mkdirs()

                                }

                                databaseEndpoints.imageEndpoint(firebaseUser.uid, uniqueDocumentId).println()
                                firebaseStorage.getReference(databaseEndpoints.imageEndpoint(firebaseUser.uid, uniqueDocumentId))
                                    .listAll()
                                    .addOnSuccessListener { imagingListResults ->

                                        imagingListResults.items.forEach { storageReferenceImageFiles ->

                                            storageReferenceImageFiles.println()

                                            val imageFileId = storageReferenceImageFiles.name

                                            storageReferenceImageFiles
                                                .getFile(File(imagingFile.getImagingFilePath(firebaseUser.uid, uniqueDocumentId, imageFileId)))

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