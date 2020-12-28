/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 9/21/20 12:13 PM
 * Last modified 9/21/20 12:09 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Preferences.Theme

import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.databinding.PreferencesControlLayoutBinding

class ToggleTheme (private val context: AppCompatActivity, private val preferencesControlViewBinding: PreferencesControlLayoutBinding) {

    val themePreferences = ThemePreferences(context)

    fun initialThemeToggleAction() {

        when (themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {
                preferencesControlViewBinding.toggleThemeView.apply {
                    speed = 3.33f
                    setMinAndMaxFrame(0, 7)
                }.playAnimation()
            }
            ThemeType.ThemeDark -> {
                preferencesControlViewBinding.toggleThemeView.apply {
                    speed = -3.33f
                    setMinAndMaxFrame(90, 99)
                }.playAnimation()
            }
        }

        toggleLightDarkTheme()

    }

    private fun toggleLightDarkTheme() {

        preferencesControlViewBinding.toggleThemeView.setOnClickListener { view ->

            preferencesControlViewBinding.toggleThemeView.also {

                when (themePreferences.checkThemeLightDark()) {
                    ThemeType.ThemeLight -> {

                        it.speed = 1.130f
                        it.setMinAndMaxFrame(7, 90)

                        if (!it.isAnimating) {
                            it.playAnimation()
                        }

                        themePreferences.changeLightDarkTheme(ThemeType.ThemeDark)

                    }
                    ThemeType.ThemeDark -> {

                        it.speed = -1.130f
                        it.setMinAndMaxFrame(7, 90)

                        if (!it.isAnimating) {
                            it.playAnimation()
                        }

                        themePreferences.changeLightDarkTheme(ThemeType.ThemeLight)

                    }
                }

            }

            when(context) {
                is PreferencesControl -> {
                    (context as PreferencesControl).preferencesLiveData.toggleTheme.postValue(true)
                }
            }

        }

        preferencesControlViewBinding.root.setOnClickListener {



        }

    }

}