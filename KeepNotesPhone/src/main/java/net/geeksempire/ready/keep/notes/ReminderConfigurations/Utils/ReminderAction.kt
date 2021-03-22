package net.geeksempire.ready.keep.notes.ReminderConfigurations.Utils

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ReminderAction : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)



        return Service.START_NOT_STICKY
    }

}