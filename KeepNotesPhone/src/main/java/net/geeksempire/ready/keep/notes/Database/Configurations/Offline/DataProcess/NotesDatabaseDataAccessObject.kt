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
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructure
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

@Dao
interface NotesDatabaseDataAccessObject {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewWidgetDataSuspend(vararg arrayOfDatabaseModels: NotesDataStructure)


    //Update Current Data
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateWidgetDataSuspend(vararg arrayOfDatabaseModels: NotesDataStructure)


    @Delete
    suspend fun deleteSuspend(databaseModel: NotesDataStructure)


    @Query("SELECT * FROM NotesDatabase ORDER BY emailAddress ASC")
    suspend fun getAllNotesDataSuspend(): List<NotesDatabaseModel>


    @Query("SELECT * FROM NotesDatabase WHERE emailAddress IN (:emailAddress) AND phoneCountryCode IN (:phoneCountryCode)")
    suspend fun loadWidgetByClassNameProviderWidgetSuspend(emailAddress: String, phoneCountryCode: String): NotesDatabaseModel


    @Query("UPDATE NotesDatabase SET emailAddress = :WidgetId WHERE emailAddress = :emailAddress AND phoneCountryCode = :phoneCountryCode")
    suspend fun updateWidgetIdByPackageNameClassNameSuspend(WidgetId: Int, emailAddress: String, phoneCountryCode: String): Int


    @Query("DELETE FROM NotesDatabase WHERE emailAddress = :emailAddress AND phoneCountryCode = :phoneCountryCode")
    suspend fun deleteByWidgetClassNameProviderWidgetSuspend(emailAddress: String, phoneCountryCode: String)


    @Query("SELECT COUNT(uniqueUsername) FROM NotesDatabase")
    suspend fun getRowCountSuspend(): Int
}