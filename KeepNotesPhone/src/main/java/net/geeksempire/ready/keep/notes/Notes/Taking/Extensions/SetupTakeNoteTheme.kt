package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R

fun TakeNote.setupTakeNoteTheme() {

    takeNoteLayoutBinding.paintingCanvasContainer.addView(paintingCanvasView)

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthSample.addView(strokePaintingCanvasView)

    when (themePreferences.checkThemeLightDark()) {
        ThemeType.ThemeLight -> {

            window.statusBarColor = getColor(R.color.light)
            window.navigationBarColor = getColor(R.color.light)

            window.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }

            takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))
            takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.lighter))

            takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.dark))

            takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))
            takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))

        }
        ThemeType.ThemeDark -> {

            window.statusBarColor = getColor(R.color.dark)
            window.navigationBarColor = getColor(R.color.dark)

            takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))
            takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.darker))

            takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.light))

            takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))
            takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))

        }
    }

}