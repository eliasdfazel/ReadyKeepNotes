package net.geeksempire.ready.keep.notes.ReminderConfigurations.IO

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.Reminder
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderDataStructure
import net.geeksempire.ready.keep.notes.ReminderConfigurations.Utils.ReminderAction
import java.util.*

class ReminderInput (private val context: AppCompatActivity, private val calendar: Calendar) {

    val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun add(reminderDataStructure: ReminderDataStructure) {

        val alarmIntent = Intent(context, ReminderAction::class.java)
        alarmIntent.putExtra(Reminder.ReminderDocumentId, reminderDataStructure.documentId)
        alarmIntent.putExtra(Reminder.ReminderDocumentTitle, reminderDataStructure.reminderTitle)
        alarmIntent.putExtra(Reminder.ReminderDocumentDescription, reminderDataStructure.reminderDescription)

        val pendingIntent = PendingIntent.getService(context, 666, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent)

    }

}