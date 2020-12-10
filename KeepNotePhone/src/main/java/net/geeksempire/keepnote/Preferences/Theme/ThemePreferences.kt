package net.geeksempire.keepnote.Preferences.Theme

import android.content.Context
import net.geeksempire.keepnote.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.keepnote.Utils.PreferencesIO.SavePreferences

class ThemePreferences (context: Context) {

    val savePreferences = SavePreferences(context)
    val readPreferences = ReadPreferences(context)

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