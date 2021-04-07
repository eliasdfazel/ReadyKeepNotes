package net.geeksempire.ready.keep.notes.SecurityConfiguratios.Checkpoint

import android.content.Context
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.SecurityConfiguratios.Utils.SecurityOptions
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.SavePreferences

class SecurityCheckpoint (private val context: Context) {

    private val savePreferences = SavePreferences(context)

    private val readPreferences = ReadPreferences(context)

    fun securityEnabled(securityEnabled: Boolean) {

        savePreferences.savePreference(SecurityOptions.SecurityData, SecurityOptions.SecurityEnabled, securityEnabled)

    }

    fun securityEnabled() : Boolean {

        return readPreferences.readPreference(SecurityOptions.SecurityData, SecurityOptions.SecurityEnabled, BuildConfig.DEBUG)
    }

}