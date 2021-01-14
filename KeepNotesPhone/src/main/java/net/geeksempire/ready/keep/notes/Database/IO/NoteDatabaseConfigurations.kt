package net.geeksempire.ready.keep.notes.Database.IO

import android.content.Context
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.SavePreferences

class NoteDatabaseConfigurations (private val context: Context) {

    fun lastTimeDatabaseUpdate(currentTime: Long = System.currentTimeMillis()) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("DatabaseConfigurations", "LastTimeUpdate", currentTime)

    }

    fun lastTimeDatabaseUpdate() : Long {

        val readPreferences = ReadPreferences(context)

        return readPreferences.readPreference("DatabaseConfigurations", "LastTimeUpdate", 0.toLong())
    }

    fun databaseSize(databaseRowCount: Long) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("DatabaseConfigurations", "DatabaseSize", databaseRowCount)

    }

    fun databaseSize() : Long {

        val readPreferences = ReadPreferences(context)

        return readPreferences.readPreference("DatabaseConfigurations", "DatabaseSize", 0.toLong())

    }

}