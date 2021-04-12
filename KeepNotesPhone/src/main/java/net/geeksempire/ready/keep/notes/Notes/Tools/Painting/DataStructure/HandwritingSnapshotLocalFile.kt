/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.Painting.DataStructure

import java.io.File

/**
 * SdcardDirectory/Uid/DocumentId/HandwritingSnapshot
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class HandwritingSnapshotLocalFile (private val baseDirectory: String) {

    fun getHandwritingSnapshotDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator
    }

    fun getHandwritingSnapshotFilePath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator +
                uniqueDocumentId + ".PNG"
    }

}