package net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure

data class ReminderContentDataStructure (var documentId: Long, var reminderTitle: String, var reminderDescription: String)

data class ReminderDataStructure (var documentId: Long, var reminderTitle: String, var reminderDescription: String,
                                  var reminderTimeYear: Int, var reminderTimeMonth: Int, var reminderTimeDay: Int,
                                  var reminderTimeHour: Int, var reminderTimeMinute: Int)