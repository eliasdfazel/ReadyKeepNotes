package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import java.io.File

/**
 * SdcardDirectory/Uid/DocumentId/HandwritingSnapshot
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class HandwritingSnapshotFile (val baseDirectory: String) {

    fun getHandwritingSnapshotFilePath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator +
                uniqueDocumentId + ".PNG"
    }

    fun getHandwritingSnapshotDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                uniqueDocumentId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator
    }

}