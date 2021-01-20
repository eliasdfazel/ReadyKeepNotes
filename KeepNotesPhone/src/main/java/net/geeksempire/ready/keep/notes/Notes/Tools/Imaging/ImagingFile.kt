package net.geeksempire.ready.keep.notes.Notes.Tools.Imaging

import java.io.File

/**
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class ImagingFile (val baseDirectory: String){

    fun getImagingDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Imaging" +
                File.separator +
                uniqueDocumentId +
                File.separator
    }

    fun getImagingFilePath(firebaseUserId: String, uniqueDocumentId: String, audioFileId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Imaging" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                audioFileId + ".JPEG"
    }
}