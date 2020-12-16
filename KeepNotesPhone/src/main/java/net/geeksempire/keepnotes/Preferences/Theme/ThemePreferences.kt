package net.geeksempire.keepnotes.Preferences.Theme

import android.content.Context
import net.geeksempire.keepnotes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.keepnotes.Utils.PreferencesIO.SavePreferences

object ThemeType {
    const val Light = true
    const val Dark = false
}

class ThemePreferences (context: Context) {

    private val savePreferences = SavePreferences(context)
    private val readPreferences = ReadPreferences(context)

    /**
     * Light = True - Dark = False
     **/
    fun checkLightDark() : Boolean {

        return readPreferences.readPreference(ThemePreferences::class.java.simpleName, "LightDark", false)
    }

    /**
     * Light = True - Dark = False
     **/
    fun changeLightDarkTheme(themeValue: Boolean) {

        savePreferences.savePreference(ThemePreferences::class.java.simpleName, "LightDark", themeValue)

    }

}