package net.geeksempire.ready.keep.notes.ReminderConfigurations.IO

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.Reminder
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderDataStructure
import net.geeksempire.ready.keep.notes.ReminderConfigurations.Utils.ReminderAction
import java.util.*

class ReminderInput (private val context: AppCompatActivity, private val calendar: Calendar) {

    private val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

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

    fun insertToCalendar(reminderDataStructure: ReminderDataStructure) {

        val contentValues = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, reminderDataStructure.documentId)

            put(CalendarContract.Events.DTSTART, calendar.timeInMillis)
            put(CalendarContract.Events.DTEND, calendar.timeInMillis + (1000 * 60))

            put(CalendarContract.Events.TITLE, reminderDataStructure.reminderTitle)
            put(CalendarContract.Events.DESCRIPTION, reminderDataStructure.reminderDescription)

            put(CalendarContract.Events.EVENT_TIMEZONE, Calendar.getInstance().timeZone.id)
        }

        val contentResolver = context.contentResolver

        val eventUri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, contentValues)

        val eventId: Long? = eventUri?.lastPathSegment?.toLong()

        Log.d(this@ReminderInput.javaClass.simpleName, "Event Id: $eventId")
    }

}