package net.geeksempire.keepnote.Notes.Taking.Extensions

import android.content.res.ColorStateList
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.Utils.UI.Display.DpToInteger

fun TakeNote.setupTakeNoteTheme() {

    takeNoteLayoutBinding.paintingCanvasContainer.addView(paintingCanvasView)

    if (themePreferences.checkLightDark()) {

        window.statusBarColor = getColor(R.color.light)
        window.navigationBarColor = getColor(R.color.light)

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.light))
        takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.lighter))

        takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.dark))

        takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))
        takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_light))

    } else {

        window.statusBarColor = getColor(R.color.dark)
        window.navigationBarColor = getColor(R.color.dark)

        takeNoteLayoutBinding.rootView.setBackgroundColor(getColor(R.color.dark))
        takeNoteLayoutBinding.editTextTitleView.setBackgroundColor(getColor(R.color.darker))

        takeNoteLayoutBinding.editTextContentView.setTextColor(getColor(R.color.light))

        takeNoteLayoutBinding.toggleKeyboardHandwriting.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))
        takeNoteLayoutBinding.savingView.backgroundTintList = ColorStateList.valueOf(getColor(R.color.default_color_dark))

    }

}

fun TakeNote.setupToggleKeyboardHandwriting() {

    takeNoteLayoutBinding.editTextContentView.post {

        takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
        takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 71)

        inputMethodManager.showSoftInput(
            takeNoteLayoutBinding.editTextContentView,
            InputMethodManager.SHOW_IMPLICIT
        )

        takeNoteLayoutBinding.editTextContentView.requestFocus()

        takeNoteLayoutBinding.editTextContentView.bringToFront()
        takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

    }

    takeNoteLayoutBinding.toggleKeyboardHandwriting.setOnClickListener {

        if (toggleKeyboardHandwriting) {

            toggleKeyboardHandwriting = false

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 71)

            inputMethodManager.showSoftInput(
                takeNoteLayoutBinding.editTextContentView,
                InputMethodManager.SHOW_IMPLICIT
            )

            takeNoteLayoutBinding.editTextContentView.requestFocus()

            takeNoteLayoutBinding.editTextContentView.bringToFront()

            takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.INVISIBLE
            takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))

            if (takeNoteLayoutBinding.colorPaletteInclude.root.isShown) {
                takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.INVISIBLE
                takeNoteLayoutBinding.colorPaletteInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
            }

        } else {

            toggleKeyboardHandwriting = true

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_handwriting)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 51)

            inputMethodManager.hideSoftInputFromWindow(
                takeNoteLayoutBinding.editTextContentView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            takeNoteLayoutBinding.editTextTitleView.clearFocus()
            takeNoteLayoutBinding.editTextContentView.clearFocus()

            takeNoteLayoutBinding.paintingCanvasContainer.bringToFront()

            takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.VISIBLE
            takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

            takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

            takeNoteLayoutBinding.colorPaletteInclude.root.bringToFront()

        }

    }

}