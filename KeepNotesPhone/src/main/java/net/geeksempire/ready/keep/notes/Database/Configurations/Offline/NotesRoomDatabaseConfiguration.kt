package net.geeksempire.ready.keep.notes.Database.Configurations.Offline

import androidx.room.Database
import androidx.room.RoomDatabase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructure
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.NotesDatabaseDataAccessObject

@Database(entities = [NotesDataStructure::class], version = 1, exportSchema = false)
abstract class NotesRoomDatabaseConfiguration : RoomDatabase() {
    abstract fun initializeDataAccessObject(): NotesDatabaseDataAccessObject
}