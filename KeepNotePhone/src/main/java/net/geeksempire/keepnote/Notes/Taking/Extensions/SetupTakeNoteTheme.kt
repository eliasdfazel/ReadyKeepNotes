package net.geeksempire.keepnote.Notes.Taking.Extensions

import android.content.res.ColorStateList
import android.view.inputmethod.InputMethodManager
import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.Notes.UI.PaintingCanvasView
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.Utils.UI.Display.DpToInteger

fun TakeNote.setupTakeNoteTheme() {

    takeNoteLayoutBinding.paintingCanvasContainer.addView(
        PaintingCanvasView(applicationContext).also { paintingCanvasView ->
            paintingCanvasView.setupPaintingPanel(
                getColor(R.color.default_color_light),
                10.0f
            )
        }
    )

    if (themePreferences.checkLightDark()) {

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))

        window.statusBarColor = getColor(R.color.light)
        window.navigationBarColor = getColor(R.color.light)

        takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.dark))

        takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))
        takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))

    } else {

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))

        window.statusBarColor = getColor(R.color.dark)
        window.navigationBarColor = getColor(R.color.dark)

        takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.light))

        takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))
        takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))

    }

}

fun TakeNote.setupToggleKeyboardHandwriting() {

    toggleKeyboardHandwriting = false

    takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
    takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 103)

    inputMethodManager.showSoftInput(
        takeNoteLayoutBinding.editTextContentView,
        InputMethodManager.SHOW_IMPLICIT
    )

    takeNoteLayoutBinding.editTextContentView.requestFocus()

    takeNoteLayoutBinding.toggleKeyboardHandwriting.setOnClickListener {

        if (toggleKeyboardHandwriting) {

            toggleKeyboardHandwriting = false

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 103)

            inputMethodManager.showSoftInput(
                takeNoteLayoutBinding.editTextContentView,
                InputMethodManager.SHOW_IMPLICIT
            )

            takeNoteLayoutBinding.editTextContentView.requestFocus()

        } else {

            toggleKeyboardHandwriting = true

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_handwriting)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 71)

            inputMethodManager.hideSoftInputFromWindow(
                takeNoteLayoutBinding.editTextContentView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            takeNoteLayoutBinding.editTextContentView.clearFocus()

        }

    }

}