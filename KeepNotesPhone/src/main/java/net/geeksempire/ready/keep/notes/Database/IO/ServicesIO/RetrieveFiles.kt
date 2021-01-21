package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints

class RetrieveFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    companion object {
        const val BaseDirectory = "BaseDirectory"

        /**
         * @param baseDirectory = externalMediaDirs[0].path
         **/
        fun startProcess(context: Context,
                         baseDirectory: String) {

            context.startService(Intent(context, RetrieveFiles::class.java).apply {
                putExtra(RetrieveFiles.BaseDirectory, baseDirectory)
            })

        }

    }

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Firebase.auth.currentUser?.let { firebaseUser ->

            intent?.let { inputData ->

                if (inputData.hasExtra(RetrieveFiles.BaseDirectory)) {

                    val firebaseStorage = Firebase.storage

                    val baseDirectory: String = inputData.getStringExtra(RetrieveFiles.BaseDirectory)!!



//                    val handwritingSnapshotFile = HandwritingSnapshotLocalFile(baseDirectory)
//
//                    firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid))
//                        .listAll()
//                        .addOnSuccessListener { listResult ->
//
//                            val noteHandwritingSnapshotFolder = File(handwritingSnapshotFile.getHandwritingSnapshotDirectoryPath(firebaseUser.uid))
//
//                            if (!noteHandwritingSnapshotFolder.exists()) {
//
//                                noteHandwritingSnapshotFolder.mkdirs()
//
//                            }
//
//                            listResult.items.forEach { storageReference ->
//
//                                storageReference
//                                    .getFile(File(handwritingSnapshotFile.getHandwritingSnapshotFilePath(firebaseUser.uid, storageReference.name)))
//
//                            }
//
//                        }
//
//                    val audioRecordingFile = AudioRecordingLocalFile(baseDirectory)
//
//                    firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid))
//                        .listAll()
//                        .addOnSuccessListener { listResult ->
//
//                            val audioRecordingDirectoryPath = File(audioRecordingFile.getAudioRecordingDirectoryPath(firebaseUser.uid))
//
//                            if (!audioRecordingDirectoryPath.exists()) {
//
//                                audioRecordingDirectoryPath.mkdirs()
//
//                            }
//
//                            listResult.items.forEach { storageReference ->
//
//                                storageReference
//                                    .getFile(File(audioRecordingFile.getAudioRecordingFilePath(firebaseUser.uid, storageReference.name)))
//
//                            }
//
//                        }
//
//                    val imagingFile = ImagingLocalFile(baseDirectory)
//
//                    firebaseStorage.getReference(databaseEndpoints.imageEndpoint(firebaseUser.uid))
//                        .listAll()
//                        .addOnSuccessListener { listResult ->
//
//                            val imagingDirectoryPath = File(imagingFile.getImagingDirectoryPath(firebaseUser.uid))
//
//                            if (!imagingDirectoryPath.exists()) {
//
//                                imagingDirectoryPath.mkdirs()
//
//                            }
//
//                            listResult.items.forEach { storageReference ->
//
//                                storageReference
//                                    .getFile(File(imagingFile.getImagingFilePath(firebaseUser.uid, storageReference.name)))
//
//                            }
//
//                        }

                }

            }

        }

        return Service.START_STICKY
    }

}