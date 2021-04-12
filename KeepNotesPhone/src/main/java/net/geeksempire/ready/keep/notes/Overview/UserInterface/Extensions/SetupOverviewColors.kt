/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

fun KeepNoteOverview.setupOverviewColors() {

    when (themePreferences.checkThemeLightDark()) {
        ThemeType.ThemeLight -> {

            window.statusBarColor = getColor(R.color.light)
            window.navigationBarColor = getColor(R.color.light)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                window.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)

            } else {

                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }

            }

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.dark_transparent_high)
            overviewLayoutBinding.textInputQuickTakeNote.boxStrokeColor = getColor(R.color.dark_transparent_higher)

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.darker))

            overviewLayoutBinding.startNewNoteView.icon = getDrawable(R.drawable.vector_brand_icon)
            overviewLayoutBinding.startNewNoteView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.lighter))

            overviewLayoutBinding.goToSearch.icon = getDrawable(R.drawable.vector_icon_search)
            overviewLayoutBinding.goToSearch.backgroundTintList = ColorStateList.valueOf(getColor(R.color.lighter))

            overviewLayoutBinding.contentImagePreview.setBackgroundColor(getColor(R.color.white))

        }
        ThemeType.ThemeDark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                window.insetsController?.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS)

            } else {

                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = 0

            }

            overviewLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

            overviewLayoutBinding.textInputQuickTakeNote.boxBackgroundColor = getColor(R.color.light_transparent_high)
            overviewLayoutBinding.textInputQuickTakeNote.boxStrokeColor = getColor(R.color.light_transparent_higher)

            overviewLayoutBinding.quickTakeNote.setTextColor(getColor(R.color.lighter))

            overviewLayoutBinding.startNewNoteView.icon = getDrawable(R.drawable.vector_brand_icon_light)
            overviewLayoutBinding.startNewNoteView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.darker))

            overviewLayoutBinding.goToSearch.icon = getDrawable(R.drawable.vector_icon_search_light)
            overviewLayoutBinding.goToSearch.backgroundTintList = ColorStateList.valueOf(getColor(R.color.darker))

            overviewLayoutBinding.contentImagePreview.setBackgroundColor(getColor(R.color.black))

        }
    }

    if (overviewAdapterUnpinned.notesDataStructureList.isNotEmpty()) {

        overviewAdapterPinned.notifyItemRangeChanged(0, overviewAdapterUnpinned.itemCount, null)

        overviewAdapterUnpinned.notifyItemRangeChanged(0, overviewAdapterUnpinned.itemCount, null)

    }

}