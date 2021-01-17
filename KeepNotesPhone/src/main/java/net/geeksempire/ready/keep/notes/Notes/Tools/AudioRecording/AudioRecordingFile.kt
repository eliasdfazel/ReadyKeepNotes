package net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording

import androidx.appcompat.app.AppCompatActivity
import java.io.File

class AudioRecordingFile (val context: AppCompatActivity){

    fun getAudioRecordingDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return context.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "AudioRecording" +
                File.separator
    }

    fun getAudioRecordingFilePath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return context.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "AudioRecording" +
                File.separator +
                uniqueDocumentId + ".MP3"
    }
}