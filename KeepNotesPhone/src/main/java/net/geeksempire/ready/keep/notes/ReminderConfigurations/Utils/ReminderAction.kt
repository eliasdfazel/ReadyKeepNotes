package net.geeksempire.ready.keep.notes.ReminderConfigurations.Utils

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import net.geeksempire.ready.keep.notes.Notes.Revealing.Mediate.PrepareDocument
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
            Log.d(this@ReminderAction.javaClass.simpleName, "Notification for Document: ${intent.getStringExtra(Reminder.ReminderDocumentId)} Created")

            val documentId = intent.getLongExtra(Reminder.ReminderDocumentId, 0.toLong())

            if (documentId == 0.toLong()) {

                notificationBuilder.create(
                    notificationChannelId = intent.getStringExtra(Reminder.ReminderDocumentId).toString(),
                    notificationTitle = "🔔 ${intent.getStringExtra(Reminder.ReminderDocumentTitle)}",
                    notificationContent = intent.getStringExtra(Reminder.ReminderDocumentDescription),
                    notificationIntent = Intent(applicationContext, PrepareDocument::class.java).apply {
                        putExtra(Reminder.ReminderDocumentId, documentId.toString())
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )

            }

        }

        return Service.START_NOT_STICKY
    }

}