package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.view.View
import android.view.animation.AnimationUtils
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.audio.recorder.AndroidAudioRecorder
import net.geeksempire.audio.recorder.model.AudioChannel
import net.geeksempire.audio.recorder.model.AudioSampleRate
import net.geeksempire.audio.recorder.model.AudioSource
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger
import java.io.File

fun TakeNote.setupToggleKeyboardHandwriting() {

    if (intent.hasExtra(TakeNote.NoteConfigurations.ExtraConfigurations)) {

       when (intent.getStringExtra(TakeNote.NoteConfigurations.ExtraConfigurations)) {
           TakeNote.NoteConfigurations.KeyboardTyping -> {

               takeNoteLayoutBinding.editTextContentView.post {

                   takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.vector_icon_keyboard)
                   takeNoteLayoutBinding.toggleKeyboardHandwriting.iconSize = DpToInteger(applicationContext, 31)

                   takeNoteLayoutBinding.editTextContentView.requestFocus()

                   showKeyboard(applicationContext, takeNoteLayoutBinding.editTextContentView)

                   takeNoteLayoutBinding.editTextContentView.bringToFront()
                   takeNoteLayoutBinding.paintingToolbarInclude.root.bringToFront()

               }

           }
           TakeNote.NoteConfigurations.Handwriting -> {

               toggleKeyboardHandwriting = true

               takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.vector_icon_handwriting)
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


               Firebase.auth.currentUser?.let {

                   audioFileId = System.currentTimeMillis().toString()
                   audioFilePath = audioRecordingLocalFile.getAudioRecordingFilePath(it.uid, documentId.toString(), audioFileId!!)

                   val audioFile = File(audioFilePath!!)

                   if (!audioFile.exists()) {

                       File(audioRecordingLocalFile.getAudioRecordingDirectoryPath(it.uid, documentId.toString())).mkdirs()

                       audioFile.createNewFile()

                   }

                   audioIO.updateAudioRecordingDatabase(documentId, audioFile.absolutePath)

                   AndroidAudioRecorder.with(this@setupToggleKeyboardHandwriting)
                       .setFilePath(audioFilePath)
                       .setColor(when (themePreferences.checkThemeLightDark()) {
                           ThemeType.ThemeLight -> {
                               getColor(R.color.default_color_bright)
                           }
                           ThemeType.ThemeDark -> {
                               getColor(R.color.default_color_dark)
                           }
                           else -> getColor(R.color.default_color)
                       })
                       .setRequestCode(TakeNote.NoteConfigurations.AudioRecordRequestCode)
                       .setSource(AudioSource.MIC)
                       .setChannel(AudioChannel.STEREO)
                       .setSampleRate(AudioSampleRate.HZ_48000)
                       .setAutoStart(true)
                       .setKeepDisplayOn(true)
                       .record()

               }

           }
       }

    } else {

        takeNoteLayoutBinding.editTextContentView.post {

            takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.vector_icon_keyboard)
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

                takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.vector_icon_keyboard)
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

                takeNoteLayoutBinding.toggleKeyboardHandwriting.icon = getDrawable(R.drawable.vector_icon_handwriting)
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