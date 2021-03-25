package net.geeksempire.ready.keep.notes.ReminderConfigurations.Utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.Reminder
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.NotificationBuilder

class ReminderAction : Service() {

    override fun onBind(intent: Intent?): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val notificationBuilder = NotificationBuilder(applicationContext)

        intent?.let {

            notificationBuilder.create(
                notificationChannelId = intent.getStringExtra(Reminder.ReminderDocumentId).toString(),
                notificationTitle = intent.getStringExtra(Reminder.ReminderDocumentTitle),
                notificationContent = intent.getStringExtra(Reminder.ReminderDocumentDescription)
            )

        }

        return Service.START_NOT_STICKY
    }

}