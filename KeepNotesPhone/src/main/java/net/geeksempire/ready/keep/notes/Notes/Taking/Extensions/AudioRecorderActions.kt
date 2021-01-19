package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.audio.recorder.AndroidAudioRecorder
import net.geeksempire.audio.recorder.model.AudioChannel
import net.geeksempire.audio.recorder.model.AudioSampleRate
import net.geeksempire.audio.recorder.model.AudioSource
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import java.io.File

fun TakeNote.setupAudioRecorderActions() {

    takeNoteLayoutBinding.audioRecordView.setOnClickListener { view ->

        Firebase.auth.currentUser?.let {

            audioFileId = System.currentTimeMillis().toString()

            audioFilePath = audioRecordingFile.getAudioRecordingFilePath(it.uid, documentId.toString(), audioFileId!!)

            val audioFile = File(audioFilePath!!)

            if (!audioFile.exists()) {

                File(audioRecordingFile.getAudioRecordingDirectoryPath(it.uid, documentId.toString())).mkdirs()

                audioFile.createNewFile()

            }

            audioIO.updateAudioRecordingDatabase(documentId, audioFile.absolutePath)

            AndroidAudioRecorder.with(this@setupAudioRecorderActions)
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