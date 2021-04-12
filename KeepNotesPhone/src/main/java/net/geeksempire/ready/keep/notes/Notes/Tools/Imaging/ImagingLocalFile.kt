/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.Imaging

import java.io.File

/**
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class ImagingLocalFile (val baseDirectory: String){

    fun getImagingDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "Imaging" +
                File.separator
    }

    fun getImagingFilePath(firebaseUserId: String, uniqueDocumentId: String, audioFileId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                File.separator +
                "Imaging" +
                File.separator +
                audioFileId
    }
}