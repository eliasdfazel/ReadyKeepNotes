package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording.AudioRecordingFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Imaging.ImagingFile
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.HandwritingSnapshotFile

class RetrieveFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    companion object {
        const val BaseDirectory = "BaseDirectory"
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

                    val audioRecordingFile = AudioRecordingFile(baseDirectory)

                    val imagingFile = ImagingFile(baseDirectory)

                    val firebaseStorage = Firebase.storage

                    firebaseStorage.getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener {



                        }

                    firebaseStorage.getReference(databaseEndpoints.voiceRecordingEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener {



                        }

                    firebaseStorage.getReference(databaseEndpoints.imageEndpoint(firebaseUser.uid))
                        .listAll()
                        .addOnSuccessListener {



                        }

                }

            }

        }

        return Service.START_STICKY
    }

}