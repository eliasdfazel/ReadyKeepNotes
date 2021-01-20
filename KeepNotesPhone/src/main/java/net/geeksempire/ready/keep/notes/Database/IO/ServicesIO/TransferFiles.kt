package net.geeksempire.ready.keep.notes.Database.IO.ServicesIO

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class TransferFiles : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val firebaseStorage = Firebase.storage



        return Service.START_STICKY
    }

}