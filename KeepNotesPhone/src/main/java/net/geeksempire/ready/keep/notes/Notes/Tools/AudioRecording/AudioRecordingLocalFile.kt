/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.AudioRecording

import java.io.File

/**
 * SdcardDirectory/Uid/DocumentId/AudioRecording
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class AudioRecordingLocalFile (val baseDirectory: String){

    fun getAudioRecordingDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "AudioRecording" +
                File.separator
    }

    fun getAudioRecordingFilePath(firebaseUserId: String, uniqueDocumentId: String, audioFileId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "AudioRecording" +
                File.separator +
                audioFileId
    }

}