package net.geeksempire.ready.keep.notes.Database.Configurations.Offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.NotesDatabaseDataAccessObject


@Database(entities = [NotesDatabaseModel::class], version = 10000, exportSchema = false)
abstract class NotesRoomDatabaseInterface : RoomDatabase() {
    abstract fun initializeDataAccessObject(): NotesDatabaseDataAccessObject
}

class NotesRoomDatabaseConfiguration {

    fun initialize(context: Context) : NotesDatabaseDataAccessObject {

        val notesRoomDatabaseConfiguration: NotesRoomDatabaseInterface = Room.databaseBuilder(context, NotesRoomDatabaseInterface::class.java, NotesDatabase)
            .fallbackToDestructiveMigration()
            .build()

        return notesRoomDatabaseConfiguration.initializeDataAccessObject()
    }

}