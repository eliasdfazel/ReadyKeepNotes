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

}