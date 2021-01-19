/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/18/20 2:39 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.Configurations.Offline.DataProcess

import androidx.room.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

@Dao
interface NotesDatabaseDataAccessObject {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompleteNewNoteData(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCompleteNoteData(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Delete
    suspend fun deleteNoteData(notesDatabaseModel: NotesDatabaseModel)


    @Query("UPDATE NotesDatabase SET noteTile = :noteTitle, noteTextContent = :noteTextContent, noteHandwritingPaintingPaths = :noteHandwritingPaintingPaths, noteEditTime = :noteEditTime, noteHandwritingSnapshotLink = :noteHandwritingSnapshotLink  WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNoteData(uniqueNoteId: Long, noteTitle: String?, noteTextContent: String?, noteHandwritingSnapshotLink: String?, noteHandwritingPaintingPaths: String?, noteEditTime: Long)


    @Query("UPDATE NotesDatabase SET noteTags = :allTags WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNoteTagsData(uniqueNoteId: Long, allTags: String)


    @Query("UPDATE NotesDatabase SET noteHandwritingPaintingPaths = :handwritingPaths WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateHandwritingPathsData(uniqueNoteId: Long, handwritingPaths: String)


    @Query("UPDATE NotesDatabase SET noteVoicePaths = :audioRecordingPaths WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateAudioRecordingPathsData(uniqueNoteId: Long, audioRecordingPaths: String)


    @Query("SELECT * FROM NotesDatabase WHERE uniqueNoteId IN (:noteUniqueIdentifier)")
    suspend fun getSpecificNoteData(noteUniqueIdentifier: Long) : NotesDatabaseModel?


    @Query("SELECT * FROM NotesDatabase ORDER BY noteIndex DESC LIMIT 1")
    suspend fun getNewestInsertedData() : NotesDatabaseModel


    @Query("SELECT * FROM NotesDatabase")
    suspend fun getAllNotesRawData() : List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase ORDER BY notePinned DESC, noteIndex DESC")
    suspend fun getAllNotesData() : List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase WHERE noteTile LIKE :searchTerm OR noteTextContent LIKE :searchTerm OR noteTags LIKE :searchTerm OR noteTranscribeTags LIKE :searchTerm ORDER BY noteIndex DESC")
    suspend fun searchAllNotesData(searchTerm: String) : List<NotesDatabaseModel>



    @Query("SELECT COUNT(uniqueNoteId) FROM NotesDatabase")
    suspend fun getSizeOfDatabase() : Int

}