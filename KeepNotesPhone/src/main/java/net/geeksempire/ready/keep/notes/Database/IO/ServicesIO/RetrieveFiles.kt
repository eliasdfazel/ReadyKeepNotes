package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints

class RetrieveFiles : Service() {

    private val databaseEndpoints = DatabaseEndpoints()

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Firebase.auth.currentUser?.let { firebaseUser ->

            Firebase.storage
                .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid))
                .listAll()

        }

        return Service.START_STICKY
    }

}