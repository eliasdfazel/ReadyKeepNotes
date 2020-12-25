package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger

fun TakeNote.setupToggleKeyboardHandwriting() {

    if (intent.hasExtra(TakeNote.NoteTakingWritingType.ExtraConfigurations)) {

       when (intent.getStringExtra(TakeNote.NoteTakingWritingType.ExtraConfigurations)) {
           TakeNote.NoteTakingWritingType.Keyboard -> {

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

           }
           TakeNote.NoteTakingWritingType.Handwriting -> {

               toggleKeyboardHandwriting = true

               takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_handwriting)
               takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 51)

               takeNoteLayoutBinding.editTextTitleView.isEnabled = false
               takeNoteLayoutBinding.editTextContentView.isEnabled = false

               takeNoteLayoutBinding.editTextTitleView.post {

                   takeNoteLayoutBinding.editTextTitleView.clearFocus()
                   takeNoteLayoutBinding.editTextContentView.clearFocus()

                   inputMethodManager.hideSoftInputFromWindow(
                       takeNoteLayoutBinding.editTextContentView.windowToken,
                       InputMethodManager.HIDE_NOT_ALWAYS
                   )

               }

               takeNoteLayoutBinding.paintingCanvasContainer.bringToFront()

               takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.VISIBLE
               takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

               takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

               takeNoteLayoutBinding.colorPaletteInclude.root.bringToFront()

           }
       }

    } else {

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

    }

    takeNoteLayoutBinding.toggleKeyboardHandwriting.setOnClickListener {

        if (toggleKeyboardHandwriting) {

            toggleKeyboardHandwriting = false

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 71)

            takeNoteLayoutBinding.editTextTitleView.isEnabled = true
            takeNoteLayoutBinding.editTextContentView.isEnabled = true

            takeNoteLayoutBinding.editTextContentView.requestFocus()

            inputMethodManager.showSoftInput(
                takeNoteLayoutBinding.editTextContentView,
                InputMethodManager.SHOW_IMPLICIT
            )

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

            takeNoteLayoutBinding.editTextTitleView.isEnabled = false
            takeNoteLayoutBinding.editTextContentView.isEnabled = false

            takeNoteLayoutBinding.editTextTitleView.clearFocus()
            takeNoteLayoutBinding.editTextContentView.clearFocus()

            inputMethodManager.hideSoftInputFromWindow(
                takeNoteLayoutBinding.editTextContentView.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )

            takeNoteLayoutBinding.paintingCanvasContainer.bringToFront()

            takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.VISIBLE
            takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

            takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

            takeNoteLayoutBinding.colorPaletteInclude.root.bringToFront()

        }

    }

}