package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions

import android.os.Build
import android.view.View
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.SearchProcess

fun SearchProcess.setupColors() {

    when (themePreferences.checkThemeLightDark()) {
        ThemeType.ThemeLight -> {

            window.statusBarColor = getColor(R.color.light)
            window.navigationBarColor = getColor(R.color.light)

            window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            searchProcessLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

            searchProcessLayoutBinding.textInputSearchTerm.boxBackgroundColor = getColor(R.color.dark_transparent_high)
            searchProcessLayoutBinding.textInputSearchTerm.boxStrokeColor = getColor(R.color.dark_transparent_higher)

            searchProcessLayoutBinding.searchTerm.setTextColor(getColor(R.color.darker))

        }
        ThemeType.ThemeDark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            searchProcessLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            searchProcessLayoutBinding.textInputSearchTerm.boxBackgroundColor = getColor(R.color.light_transparent_high)
            searchProcessLayoutBinding.textInputSearchTerm.boxStrokeColor = getColor(R.color.light_transparent_higher)

            searchProcessLayoutBinding.searchTerm.setTextColor(getColor(R.color.lighter))

        }
    }

}