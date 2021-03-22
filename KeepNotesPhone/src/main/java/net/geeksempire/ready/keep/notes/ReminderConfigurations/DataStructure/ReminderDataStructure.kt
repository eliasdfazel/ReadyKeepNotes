package net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure

object Reminder {
    const val ReminderDocumentId = "ReminderDocumentId"
    const val ReminderDocumentTitle = "ReminderDocumentTitle"
    const val ReminderDocumentDescription = "ReminderDocumentDescription"
}

data class ReminderContentDataStructure (var documentId: Long, var reminderTitle: String, var reminderDescription: String)

data class ReminderDataStructure (var documentId: Long, var reminderTitle: String, var reminderDescription: String,
                                  var reminderTimeYear: Int, var reminderTimeMonth: Int, var reminderTimeDay: Int,
                                  var reminderTimeHour: Int, var reminderTimeMinute: Int)