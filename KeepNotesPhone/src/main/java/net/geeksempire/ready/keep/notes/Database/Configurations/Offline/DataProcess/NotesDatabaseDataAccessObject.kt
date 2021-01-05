/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 5/18/20 2:39 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geekstools.floatshort.PRO.Widgets.RoomDatabase

import androidx.room.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

@Dao
interface NotesDatabaseDataAccessObject {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewNoteData(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNoteData(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Delete
    suspend fun deleteSuspend(notesDatabaseModel: NotesDatabaseModel)


    @Query("UPDATE NotesDatabase SET noteTags = :allTags WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateNoteTagsData(uniqueNoteId: Long, allTags: String)


    @Query("UPDATE NotesDatabase SET noteHandwritingPaintingPaths = :handwritingPaths WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun updateHandwritingPathsData(uniqueNoteId: String, handwritingPaths: String)


    @Query("SELECT * FROM NotesDatabase ORDER BY noteIndex DESC")
    suspend fun getAllNotesData(): List<NotesDatabaseModel>


    @Query("SELECT COUNT(uniqueNoteId) FROM NotesDatabase")
    suspend fun getSizeOfDatabase(): Int

}