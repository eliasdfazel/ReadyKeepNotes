package net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording

import java.io.File

/**
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class AudioRecordingFile (val baseDirectory: String){

    fun getAudioRecordingDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "AudioRecording" +
                File.separator +
                uniqueDocumentId +
                File.separator
    }

    fun getAudioRecordingFilePath(firebaseUserId: String, uniqueDocumentId: String, audioFileId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "AudioRecording" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                audioFileId + ".MP3"
    }
}