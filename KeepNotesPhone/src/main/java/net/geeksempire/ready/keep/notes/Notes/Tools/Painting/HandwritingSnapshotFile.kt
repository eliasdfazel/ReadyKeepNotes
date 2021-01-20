package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import java.io.File

/**
 * @param baseDirectory = activity.externalMediaDirs[0].path
 **/
class HandwritingSnapshotFile (val baseDirectory: String) {

    fun getHandwritingSnapshotDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator +
                uniqueDocumentId + ".PNG"
    }

    fun getHandwritingSnapshotFilePath(firebaseUserId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator
    }

}