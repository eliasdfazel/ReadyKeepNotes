package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import java.io.File

fun TakeNote.setupAudioRecorderActions() {

    takeNoteLayoutBinding.audioRecordView.setOnClickListener { view ->

        Firebase.auth.currentUser?.let {

            val audioFilePath: String = audioRecordingFile.getAudioRecordingFilePath(it.uid, documentId.toString())

            val audioFile = File(audioFilePath)

            if (!audioFile.exists()) {

                File(audioRecordingFile.getAudioRecordingDirectoryPath(it.uid, documentId.toString())).mkdirs()

                audioFile.createNewFile()

            }

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