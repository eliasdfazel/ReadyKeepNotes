/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Preferences.Theme

import android.content.Context
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.SavePreferences

object ThemeType {
    const val ThemeLight = true
    const val ThemeDark = false
}

class ThemePreferences (context: Context) {

    private val savePreferences = SavePreferences(context)
    private val readPreferences = ReadPreferences(context)

    /**
     * Light = True - Dark = False
     **/
    fun checkThemeLightDark() : Boolean {

        return readPreferences.readPreference(
            ThemePreferences::class.java.simpleName, "LightDark",
            ThemeType.ThemeDark
        )
    }

    /**
     * Light = True - Dark = False
     **/
    fun changeLightDarkTheme(themeValue: Boolean) {

        savePreferences.savePreference(ThemePreferences::class.java.simpleName, "LightDark", themeValue)

    }

}