package net.geeksempire.ready.keep.notes.Database.IO

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Utils.Data.forEach
import org.json.JSONArray

class AudioIO (private val keepNoteApplication: KeepNoteApplication) {

    fun updateAudioRecordingDatabase(documentId: Long, newAudioRecordingFilePath: String) = CoroutineScope(Dispatchers.IO).launch {

        val audioRecordingFilePaths = ArrayList<String>()

        val notesRoomDatabaseConfiguration = keepNoteApplication.notesRoomDatabaseConfiguration

        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration
            .prepareRead()

        audioRecordingFilePaths.add(newAudioRecordingFilePath)

        notesDatabaseDataAccessObject.getSpecificNoteData(documentId)?.let { audioRecordedRawJsonIO ->

            if (!audioRecordedRawJsonIO.noteVoiceContent.isNullOrBlank()) {

                val audioRecordedJsonArray = JSONArray(audioRecordedRawJsonIO.noteVoiceContent)
                audioRecordedJsonArray.forEach {

                    audioRecordingFilePaths.add(it.toString())

                }

            }

            notesDatabaseDataAccessObject.updateAudioRecordingPathsData(documentId, keepNoteApplication.jsonIO.writeAudioRecordingFilePaths(audioRecordingFilePaths))

        }

        if (notesDatabaseDataAccessObject.getSpecificNoteData(documentId) == null) {

            val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                noteTile = null,
                noteTextContent = null,
                noteHandwritingPaintingPaths = null,
                noteHandwritingSnapshotLink = null,
                noteTakenTime = documentId,
                noteEditTime = null,
                noteIndex = documentId,
                noteTags = null,
                dataSelected = NotesTemporaryModification.NoteIsNotSelected
            )

            notesDatabaseDataAccessObject.insertNewNoteData(notesDatabaseModel)

        }

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}