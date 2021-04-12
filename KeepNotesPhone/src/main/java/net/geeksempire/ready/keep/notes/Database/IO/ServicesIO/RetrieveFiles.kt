/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

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
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.DataStructure.HandwritingSnapshotLocalFile
import net.geeksempire.ready.keep.notes.Utils.Extensions.println
import java.io.File

class RetrieveFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    var totalProcess = 0
    var allProcessCounter = 0

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

                    /*startForeground(RetrieveFiles.Foreground.NotificationId,
                        NotificationBuilder(applicationContext)
                            .create(notificationId = RetrieveFiles.Foreground.NotificationId, notificationChannelId = this@RetrieveFiles.javaClass.simpleName,
                                notificationTitle = getString(R.string.applicationName), notificationContent = getString(R.string.retrievingFilesText)))*/

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
                                    .addOnSuccessListener {

                                        totalProcess++

                                        allProcessCounter++

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

                                        totalProcess.plus(voiceRecordingListResults.items.size)

                                        voiceRecordingListResults.items.forEach { storageReferenceAudioFiles ->

                                            storageReferenceAudioFiles.println()

                                            val audioFileId = storageReferenceAudioFiles.name

                                            storageReferenceAudioFiles
                                                .getFile(File(audioRecordingFile.getAudioRecordingFilePath(firebaseUser.uid, uniqueDocumentId, audioFileId)))
                                                .addOnSuccessListener {

                                                    allProcessCounter++

                                                }

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

                                        totalProcess.plus(imagingListResults.items.size)

                                        imagingListResults.items.forEach { storageReferenceImageFiles ->

                                            storageReferenceImageFiles.println()

                                            val imageFileId = storageReferenceImageFiles.name

                                            storageReferenceImageFiles
                                                .getFile(File(imagingFile.getImagingFilePath(firebaseUser.uid, uniqueDocumentId, imageFileId)))
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