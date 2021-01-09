package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.SearchProcess
import net.geeksempire.ready.keep.notes.Utils.UI.Animations.AnimationListener
import net.geeksempire.ready.keep.notes.Utils.UI.Animations.CircularRevealAnimation
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY

fun SearchProcess.setupViews() {

    val animationListener = object : AnimationListener {

        override fun animationFinished() {
            super.animationFinished()

            searchProcessLayoutBinding.searchTerm.requestFocus()
            showKeyboard(applicationContext, searchProcessLayoutBinding.searchTerm)

        }

    }

    CircularRevealAnimation(animationListener).run {

        startForActivityRoot(
            activity = this@setupViews,
            xPosition = if (intent.hasExtra("xPosition")) { intent.getIntExtra("xPosition", displayX(applicationContext) / 2) } else { displayX(applicationContext) / 2 },
            yPosition = if (intent.hasExtra("yPosition")) {intent.getIntExtra("yPosition", displayY(applicationContext) / 2)} else { displayY(applicationContext) / 2 }
        )

    }

}

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

            searchProcessLayoutBinding.searchActionView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.dark_transparent_high))

            searchProcessLayoutBinding.brandView.imageTintList = ColorStateList.valueOf(getColor(R.color.dark))

        }
        ThemeType.ThemeDark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            searchProcessLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            searchProcessLayoutBinding.textInputSearchTerm.boxBackgroundColor = getColor(R.color.light_transparent_high)
            searchProcessLayoutBinding.textInputSearchTerm.boxStrokeColor = getColor(R.color.light_transparent_higher)

            searchProcessLayoutBinding.searchTerm.setTextColor(getColor(R.color.lighter))

            searchProcessLayoutBinding.searchActionView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.light_transparent_high))

            searchProcessLayoutBinding.brandView.imageTintList = ColorStateList.valueOf(getColor(R.color.light))

        }
    }

}