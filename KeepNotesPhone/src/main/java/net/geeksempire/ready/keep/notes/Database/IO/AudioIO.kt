/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.IO

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
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

        val specificNoteData = notesDatabaseDataAccessObject.getSpecificNoteData(documentId)

        if (specificNoteData == null) {

            val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                noteTile = null,
                noteTextContent = null,
                noteHandwritingPaintingPaths = null,
                noteHandwritingSnapshotLink = null,
                noteVoicePaths = keepNoteApplication.jsonIO.writeAudioRecordingFilePaths(audioRecordingFilePaths),
                noteImagePaths = null,
                noteGifPaths = null,
                noteTakenTime = documentId,
                noteEditTime = null,
                noteIndex = documentId,
                noteTags = null,
                noteHashTags = null,
                noteTranscribeTags = null
            )

            notesDatabaseDataAccessObject.insertCompleteNewNoteData(notesDatabaseModel)

        } else {

            if (!specificNoteData.noteVoicePaths.isNullOrBlank()) {

                val audioRecordedJsonArray = JSONArray(specificNoteData.noteVoicePaths)
                audioRecordedJsonArray.forEach {

                    audioRecordingFilePaths.add(it.toString())

                }

            }

            notesDatabaseDataAccessObject.updateAudioRecordingPathsData(documentId, keepNoteApplication.jsonIO.writeAudioRecordingFilePaths(audioRecordingFilePaths))

        }

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}