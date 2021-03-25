package net.geeksempire.ready.keep.notes.ReminderConfigurations.UserInterface

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderContentDataStructure
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderDataStructure
import net.geeksempire.ready.keep.notes.ReminderConfigurations.IO.ReminderInput
import java.util.*

class ReminderTimeDialogue(private val context: AppCompatActivity, private val themePreferences: ThemePreferences) {

    private val calendar = Calendar.getInstance()

    private lateinit var datePickerDialog: DatePickerDialog

    private lateinit var timePickerDialog: TimePickerDialog

    fun initialize(reminderContentDataStructure: ReminderContentDataStructure) : ReminderTimeDialogue {

        datePickerDialog = DatePickerDialog(
            context,
            when (themePreferences.checkThemeLightDark()) {
                ThemeType.ThemeLight -> {

                    R.style.TimeDialogue_Light

                }
                ThemeType.ThemeDark -> {

                    R.style.TimeDialogue_Dark

                }
                else -> R.style.TimeDialogue_Light
            },
            { datePicker, year, month, dayOfMonth ->

                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)


                try {

                    timePickerDialog.dismiss()

                } catch (e: Exception) {
                  e.printStackTrace()
                } finally {

                    timePickerDialog.show()

                }

            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

        timePickerDialog = TimePickerDialog(
            context,
            when (themePreferences.checkThemeLightDark()) {
                ThemeType.ThemeLight -> {

                    R.style.TimeDialogue_Light

                }
                ThemeType.ThemeDark -> {

                    R.style.TimeDialogue_Dark

                }
                else -> R.style.TimeDialogue_Light
            },
            { timePicker, hours, minutes ->

                calendar.set(Calendar.HOUR_OF_DAY, hours)
                calendar.set(Calendar.MINUTE, minutes)
                calendar.set(Calendar.SECOND, 13)

                val reminderDataStructure = ReminderDataStructure(documentId = reminderContentDataStructure.documentId,
                    reminderTitle = reminderContentDataStructure.reminderTitle, reminderDescription = reminderContentDataStructure.reminderDescription,
                    reminderTimeYear = calendar[Calendar.YEAR], reminderTimeMonth = calendar[Calendar.MONTH], reminderTimeDay = calendar[Calendar.DAY_OF_MONTH],
                    reminderTimeHour = calendar[Calendar.HOUR_OF_DAY], reminderTimeMinute = calendar[Calendar.MINUTE])

                /*
                *
                * Save To Offline Database
                */
                

                /*
                * Save To Firestore
                *
                */

                val jsonIO = JsonIO()
                jsonIO.writeReminderData(reminderDataStructure)

                ReminderInput(context, calendar).apply {
                    add(reminderDataStructure)
                    insertToCalendar(reminderDataStructure)
                }

                try {

                    timePickerDialog.dismiss()

                } catch (e: Exception) {
                    e.printStackTrace()
                }

            },
            calendar[Calendar.HOUR_OF_DAY],
            calendar[Calendar.MINUTE],
            true
        )

        return this@ReminderTimeDialogue
    }

    fun show() {

        showCalendar()

    }

    fun showCalendar() {

        datePickerDialog.show()

    }

    fun showTime() {

        timePickerDialog.show()

    }

}