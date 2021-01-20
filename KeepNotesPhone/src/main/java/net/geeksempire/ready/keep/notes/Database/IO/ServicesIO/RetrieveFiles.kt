package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording.AudioRecordingFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Imaging.ImagingFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.HandwritingSnapshotFile
import java.io.File

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

                    val baseDirectory: String = inputData.getStringExtra(RetrieveFiles.BaseDirectory)!!

                    val handwritingSnapshotFile = HandwritingSnapshotFile(baseDirectory)

                    val firebaseStorage = Firebase.storage

                    firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener { listResult ->

                            val noteHandwritingSnapshotFolder = File(handwritingSnapshotFile.getHandwritingSnapshotDirectoryPath(firebaseUser.uid))

                            if (!noteHandwritingSnapshotFolder.exists()) {

                                noteHandwritingSnapshotFolder.mkdirs()

                            }

                            listResult.items.forEach { storageReference ->

                                storageReference
                                    .getFile(File(handwritingSnapshotFile.getHandwritingSnapshotFilePath(firebaseUser.uid, storageReference.name)))

                            }

                        }

                    val audioRecordingFile = AudioRecordingFile(baseDirectory)

                    firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener { listResult ->



                        }

                    val imagingFile = ImagingFile(baseDirectory)

                    firebaseStorage.getReference(databaseEndpoints.imageEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener { listResult ->



                        }

                }

            }

        }

        return Service.START_STICKY
    }

}