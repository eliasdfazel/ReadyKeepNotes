package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.view.View
import android.view.animation.AnimationUtils
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger

fun TakeNote.setupToggleKeyboardHandwriting() {

    if (intent.hasExtra(TakeNote.NoteConfigurations.ExtraConfigurations)) {

       when (intent.getStringExtra(TakeNote.NoteConfigurations.ExtraConfigurations)) {
           TakeNote.NoteConfigurations.KeyboardTyping -> {

               takeNoteLayoutBinding.editTextContentView.post {

                   takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
                   takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 31)

                   showKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

                   takeNoteLayoutBinding.editTextContentView.requestFocus()

                   takeNoteLayoutBinding.editTextContentView.bringToFront()
                   takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

               }

           }
           TakeNote.NoteConfigurations.Handwriting -> {

               toggleKeyboardHandwriting = true

               takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_handwriting)
               takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 51)

               takeNoteLayoutBinding.editTextTitleView.isEnabled = false
               takeNoteLayoutBinding.editTextContentView.isEnabled = false

               takeNoteLayoutBinding.editTextTitleView.post {

                   takeNoteLayoutBinding.editTextTitleView.clearFocus()
                   takeNoteLayoutBinding.editTextContentView.clearFocus()

                   hideKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

               }

               takeNoteLayoutBinding.paintingCanvasContainer.bringToFront()

               takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.VISIBLE
               takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

               takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

               takeNoteLayoutBinding.colorPaletteInclude.root.bringToFront()

           }
           TakeNote.NoteConfigurations.VoiceRecording ->{



           }
       }

    } else {

        takeNoteLayoutBinding.editTextContentView.post {

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
            takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 31)

            showKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

            takeNoteLayoutBinding.editTextContentView.requestFocus()

            takeNoteLayoutBinding.editTextContentView.bringToFront()
            takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

        }

    }

    takeNoteLayoutBinding.toggleKeyboardHandwriting.setOnClickListener {

        if (!contentDescriptionShowing) {

            if (toggleKeyboardHandwriting) {

                toggleKeyboardHandwriting = false

                takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.icon_keyboard)
                takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 31)

                takeNoteLayoutBinding.editTextTitleView.isEnabled = true
                takeNoteLayoutBinding.editTextContentView.isEnabled = true

                takeNoteLayoutBinding.editTextContentView.requestFocus()

                showKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

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

                hideKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

                takeNoteLayoutBinding.paintingCanvasContainer.bringToFront()

                takeNoteLayoutBinding.paintingToolbarInclude.root.visibility = View.VISIBLE
                takeNoteLayoutBinding.paintingToolbarInclude.root.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))

                takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

                takeNoteLayoutBinding.colorPaletteInclude.root.bringToFront()

            }

        } else {



        }

    }

}