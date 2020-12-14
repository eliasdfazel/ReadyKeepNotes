package net.geeksempire.keepnote.Notes.Taking.Extensions

import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.Utils.UI.Display.DpToInteger

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