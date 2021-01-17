package net.geeksempire.ready.keep.notes.Notes.Tools.Painting

import androidx.appcompat.app.AppCompatActivity
import java.io.File

class HandwritingSnapshotFile (val context: AppCompatActivity) {

    fun getHandwritingSnapshotDirectoryPath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return context.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator
    }

    fun getHandwritingSnapshotFilePath(firebaseUserId: String, uniqueDocumentId: String) : String {

        return context.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "HandwritingSnapshot" +
                File.separator +
                uniqueDocumentId + ".PNG"
    }

}