package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import cafe.adriel.androidaudiorecorder.AndroidAudioRecorder
import cafe.adriel.androidaudiorecorder.model.AudioChannel
import cafe.adriel.androidaudiorecorder.model.AudioSampleRate
import cafe.adriel.androidaudiorecorder.model.AudioSource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R
import java.io.File

fun TakeNote.setupAudioRecorderActions() {

    takeNoteLayoutBinding.audioRecordView.setOnClickListener { view ->

        Firebase.auth.currentUser?.let {

            val audioFilePath: String = externalMediaDirs[0].path +
                    File.separator +
                    it.uid +
                    File.separator +
                    "AudioRecording" +
                    File.separator +
                    documentId + ".MP3"

            val audioFile = File(audioFilePath)

            if (!audioFile.exists()) {

                File(externalMediaDirs[0].path + File.separator + it.uid + File.separator + "AudioRecording" + File.separator).mkdirs()

                audioFile.createNewFile()

            }

            AndroidAudioRecorder.with(this@setupAudioRecorderActions)
                .setFilePath(audioFilePath)
                .setColor(getColor(R.color.pink))
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