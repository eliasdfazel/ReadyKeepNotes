package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderContentDataStructure
import net.geeksempire.ready.keep.notes.ReminderConfigurations.UserInterface.ReminderTimeDialogue

fun TakeNote.setupNoteReminder() {

    takeNoteLayoutBinding.setReminderView.setOnClickListener {

        ReminderTimeDialogue(this@setupNoteReminder, themePreferences)
            .initialize(ReminderContentDataStructure(documentId = documentId,
                reminderTitle = takeNoteLayoutBinding.editTextTitleView.text.toString(), reminderDescription = takeNoteLayoutBinding.editTextContentView.text.toString())
            )
            .show()

    }

}