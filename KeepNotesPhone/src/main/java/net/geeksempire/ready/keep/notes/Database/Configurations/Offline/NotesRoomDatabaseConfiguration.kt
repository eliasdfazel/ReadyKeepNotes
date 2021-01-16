package net.geeksempire.ready.keep.notes.Database.Configurations.Offline

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import net.geeksempire.ready.keep.notes.Database.Configurations.Offline.DataProcess.NotesDatabaseDataAccessObject
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabase
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

@Database(entities = [NotesDatabaseModel::class], version = 10000, exportSchema = false)
abstract class NotesRoomDatabaseInterface : RoomDatabase() {
    abstract fun initializeDataAccessObject(): NotesDatabaseDataAccessObject
}

class NotesRoomDatabaseConfiguration (private val context: Context) {

    private lateinit var databaseInterface: NotesRoomDatabaseInterface

    private fun initialize() : RoomDatabase.Builder<NotesRoomDatabaseInterface> {

        return Room.databaseBuilder(context, NotesRoomDatabaseInterface::class.java, NotesDatabase)
            .fallbackToDestructiveMigration().addCallback(object : RoomDatabase.Callback() {

                override fun onCreate(supportSQLiteDatabase: SupportSQLiteDatabase) {
                    super.onCreate(supportSQLiteDatabase)
                }

                override fun onOpen(supportSQLiteDatabase: SupportSQLiteDatabase) {
                    super.onOpen(supportSQLiteDatabase)
                }

            })
    }

    fun prepareRead() : NotesDatabaseDataAccessObject {

        databaseInterface = initialize().build()

        return databaseInterface.initializeDataAccessObject()
    }

    fun closeDatabase() {

        if (::databaseInterface.isInitialized) {

            if (databaseInterface.isOpen) {

                databaseInterface.close()

            }

        }

    }

}