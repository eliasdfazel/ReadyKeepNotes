package net.geeksempire.ready.keep.notes.Database.IO

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Utils.Data.forEach
import org.json.JSONArray

class AudioIO (private val keepNoteApplication: KeepNoteApplication) {

    fun updateAudioRecordingDatabase(documentId: Long, newAudioRecordingFilePath: String) = CoroutineScope(Dispatchers.IO).launch {

        val audioRecordingFilePaths = ArrayList<String>()

        val notesRoomDatabaseConfiguration = keepNoteApplication.notesRoomDatabaseConfiguration

        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration
            .prepareRead()

        val audioRecordedRawJsonIO = notesDatabaseDataAccessObject.getSpecificNoteData(documentId)


        audioRecordingFilePaths.add(newAudioRecordingFilePath)

        if (!audioRecordedRawJsonIO.noteVoiceContent.isNullOrBlank()) {

            val audioRecordedJsonArray = JSONArray(audioRecordedRawJsonIO.noteVoiceContent)
            audioRecordedJsonArray.forEach {

                audioRecordingFilePaths.add(it.toString())

            }

        }

        notesDatabaseDataAccessObject.updateAudioRecordingPathsData(documentId, keepNoteApplication.jsonIO.writeAudioRecordingFilePaths(audioRecordingFilePaths))

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}