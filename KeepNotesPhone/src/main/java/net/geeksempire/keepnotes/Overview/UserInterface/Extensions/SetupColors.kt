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

            window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.dark_transparent_high)

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.darker))

        }
        ThemeType.Dark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.light_transparent_high)

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.lighter))

        }
    }


}