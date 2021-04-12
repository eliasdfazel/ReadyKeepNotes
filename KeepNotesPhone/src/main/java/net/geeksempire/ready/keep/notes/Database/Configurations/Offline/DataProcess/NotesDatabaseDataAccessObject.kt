/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.Configurations.Offline.DataProcess

import androidx.room.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification

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


    @Query("UPDATE NotesDatabase SET noteIndex = :noteIndex WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNoteIndex(uniqueNoteId: Long, noteIndex: Long)


    @Query("UPDATE NotesDatabase SET noteTags = :allTags WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNoteTagsData(uniqueNoteId: Long, allTags: String)


    /**
     * Unpinned = 0 - NotesTemporaryModification.NotePinned | Pinned = 1 - NotesTemporaryModification.NotePinned
     **/
    @Query("UPDATE NotesDatabase SET notePinned = :notePinned WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNotePinnedData(uniqueNoteId: Long, notePinned: Int)


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


    /**
     * Unpinned = 0 - NotesTemporaryModification.NotePinned | Pinned = 1 - NotesTemporaryModification.NotePinned
     **/
    @Query("SELECT * FROM NotesDatabase WHERE notePinned = :pinStatus ORDER BY noteIndex DESC")
    suspend fun getAllPinnedNotesData(pinStatus: Int = NotesTemporaryModification.NotePinned) : List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase ORDER BY notePinned DESC, noteIndex DESC")
    suspend fun getAllNotesData() : List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase WHERE noteTile LIKE :searchTerm OR noteTextContent LIKE :searchTerm OR noteTags LIKE :searchTerm OR noteTranscribeTags LIKE :searchTerm ORDER BY noteIndex DESC")
    suspend fun searchAllNotesData(searchTerm: String) : List<NotesDatabaseModel>


    @Query("SELECT COUNT(uniqueNoteId) FROM NotesDatabase")
    suspend fun getSizeOfDatabase() : Int

}