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
    suspend fun insertNewWidgetDataSuspend(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWidgetDataSuspend(vararg arrayOfNotesDatabaseModels: NotesDatabaseModel)


    @Delete
    suspend fun deleteSuspend(notesDatabaseModel: NotesDatabaseModel)


    @Query("SELECT * FROM NotesDatabase ORDER BY noteTakenTime ASC")
    suspend fun getAllNotesDataSuspend(): List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun loadSpecificNote(uniqueNoteId: Long): NotesDatabaseModel


    @Query("UPDATE NotesDatabase SET uniqueNoteId = :uniqueNoteId")
    suspend fun updateSpecificNote(uniqueNoteId: Long): Int


    @Query("DELETE FROM NotesDatabase WHERE uniqueNoteId = :uniqueNoteId")
    suspend fun deleteSpecificNote(uniqueNoteId: Long)


    @Query("SELECT COUNT(uniqueNoteId) FROM NotesDatabase")
    suspend fun getRowCountSuspend(): Int
}