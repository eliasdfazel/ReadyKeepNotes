package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import java.io.File

/**
 * SdcardDirectory/Uid/DocumentId/HandwritingSnapshot
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class HandwritingSnapshotLocalFile (val baseDirectory: String) {

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