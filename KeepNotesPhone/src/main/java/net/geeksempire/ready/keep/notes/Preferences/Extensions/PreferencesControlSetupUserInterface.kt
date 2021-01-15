/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 9/29/20 12:59 PM
 * Last modified 9/29/20 12:54 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Preferences.Extensions

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import androidx.constraintlayout.widget.ConstraintLayout
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.Preferences.Theme.ToggleTheme
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.navigationBarHeight
import net.geeksempire.ready.keep.notes.Utils.UI.Display.statusBarHeight

fun PreferencesControl.preferencesControlSetupUserInterface() {

    val toggleTheme = ToggleTheme(this@preferencesControlSetupUserInterface, preferencesControlLayoutBinding)
    toggleTheme.initialThemeToggleAction()

    toggleLightDark()

    preferencesControlLayoutBinding.rootContainer.setPadding(0, preferencesControlLayoutBinding.rootContainer.paddingTop + statusBarHeight(applicationContext), 0, 0)

    val rootContainerLayoutParams = preferencesControlLayoutBinding.rootContainer.layoutParams as ConstraintLayout.LayoutParams
    rootContainerLayoutParams.setMargins(0, 0, 0, navigationBarHeight(applicationContext))
    preferencesControlLayoutBinding.rootContainer.layoutParams = rootContainerLayoutParams

}

fun PreferencesControl.toggleLightDark() {

    when (themePreferences.checkThemeLightDark()) {
        ThemeType.ThemeLight -> {

            preferencesControlLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

            preferencesControlLayoutBinding.userDisplayName.setTextColor(getColor(R.color.dark))

            val accountViewBackground = getDrawable(R.drawable.preferences_account_view_background) as LayerDrawable
            val gradientDrawable = (accountViewBackground.findDrawableByLayerId(R.id.temporaryBackground) as GradientDrawable)
            gradientDrawable.colors = intArrayOf(getColor(R.color.dark), getColor(R.color.dark_transparent), getColor(R.color.dark_blurry_color))
            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            accountViewBackground.findDrawableByLayerId(R.id.temporaryForeground).setTint(getColor(R.color.light))

            preferencesControlLayoutBinding.accountManagerView.background = accountViewBackground

        }
        ThemeType.ThemeDark -> {

            preferencesControlLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            preferencesControlLayoutBinding.userDisplayName.setTextColor(getColor(R.color.light))

            val accountViewBackground = getDrawable(R.drawable.preferences_account_view_background) as LayerDrawable
            val gradientDrawable = (accountViewBackground.findDrawableByLayerId(R.id.temporaryBackground) as GradientDrawable)
            gradientDrawable.colors = intArrayOf(getColor(R.color.light), getColor(R.color.light_transparent), getColor(R.color.light_blurry_color))
            gradientDrawable.gradientType = GradientDrawable.LINEAR_GRADIENT
            accountViewBackground.findDrawableByLayerId(R.id.temporaryForeground).setTint(getColor(R.color.dark))

            preferencesControlLayoutBinding.accountManagerView.background = accountViewBackground

        }
    }

    preferencesControlLayoutBinding.whatsNewView.background = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,
        intArrayOf(
            getColor(R.color.blue),
            Color.TRANSPARENT,
            getColor(R.color.pink)
        ))

}