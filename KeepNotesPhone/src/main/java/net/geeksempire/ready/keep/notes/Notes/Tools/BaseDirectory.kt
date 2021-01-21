package net.geeksempire.ready.keep.notes.Notes.Tools

import androidx.appcompat.app.AppCompatActivity
import java.io.File

class BaseDirectory {

    fun localBaseDirectory(activity: AppCompatActivity, firebaseUserId: String, uniqueDocumentId: String) : String {

        return activity.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId
    }

}