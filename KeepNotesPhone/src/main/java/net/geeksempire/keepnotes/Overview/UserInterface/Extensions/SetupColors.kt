package net.geeksempire.keepnotes.Overview.UserInterface.Extensions

import android.os.Build
import android.view.View
import net.geeksempire.keepnotes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.keepnotes.Preferences.Theme.ThemeType
import net.geeksempire.keepnotes.R


fun KeepNoteOverview.setupColors() {

    when (themePreferences.checkLightDark()) {
        ThemeType.Light -> {

            window.statusBarColor = getColor(R.color.light)
            window.navigationBarColor = getColor(R.color.light)

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.default_color))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.dark_transparent_high)

            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }

        }
        ThemeType.Dark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.light_transparent_high)

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.default_color))

        }
    }


}