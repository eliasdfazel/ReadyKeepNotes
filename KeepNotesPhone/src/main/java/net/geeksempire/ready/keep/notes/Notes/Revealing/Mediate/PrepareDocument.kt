package net.geeksempire.ready.keep.notes.Notes.Revealing.Mediate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.Reminder

class PrepareDocument : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.hasExtra(Reminder.ReminderDocumentId)) {

            val documentId = intent.getStringExtra(Reminder.ReminderDocumentId)!!

            CoroutineScope(Dispatchers.IO).launch {

                val notesDatabaseDataAccessObject = (application as KeepNoteApplication).notesRoomDatabaseConfiguration.prepareRead()

                val specificDocument = notesDatabaseDataAccessObject.getSpecificNoteData(documentId.toLong())

                specificDocument?.let {

                    val paintingPathsJsonArray = specificDocument.noteHandwritingPaintingPaths

                    if (paintingPathsJsonArray.isNullOrEmpty()) {

                        TakeNote.open(context = applicationContext,
                            incomingActivityName = this@PrepareDocument.javaClass.simpleName,
                            extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                            uniqueNoteId = documentId.toLong(),
                            noteTile = specificDocument.noteTile,
                            contentText = specificDocument.noteTextContent,
                            encryptedTextContent = true,
                            updateExistingNote = true,
                            pinnedNote = when (specificDocument.notePinned) {
                                NotesTemporaryModification.NoteUnpinned -> {
                                    false
                                }
                                NotesTemporaryModification.NotePinned -> {
                                    true
                                } else -> {
                                    false
                                }
                            }
                        )

                    } else {

                        TakeNote.open(context = applicationContext,
                            incomingActivityName = this@PrepareDocument.javaClass.simpleName,
                            extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                            uniqueNoteId = documentId.toLong(),
                            noteTile = specificDocument.noteTile,
                            contentText = specificDocument.noteTextContent,
                            paintingPath = paintingPathsJsonArray,
                            encryptedTextContent = true,
                            updateExistingNote = true,
                            pinnedNote = when (specificDocument.notePinned) {
                                NotesTemporaryModification.NoteUnpinned -> {
                                    false
                                }
                                NotesTemporaryModification.NotePinned -> {
                                    true
                                } else -> {
                                    false
                                }
                            }
                        )

                    }

                }

                this@PrepareDocument.finish()

            }
            
        } else {

            this@PrepareDocument.finish()

        }

    }

}