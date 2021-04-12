/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Tools.Directory

import androidx.appcompat.app.AppCompatActivity
import java.io.File

class BaseDirectory {

    /**
     * @param baseDirectory = activity.externalMediaDirs[0].path
     **/
    fun localBaseDirectory(baseDirectory: String, firebaseUserId: String) : String {

        return baseDirectory +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes"
    }

    fun localBaseSpecificDirectory(activity: AppCompatActivity, firebaseUserId: String, uniqueDocumentId: String) : String {

        return activity.externalMediaDirs[0].path +
                File.separator +
                firebaseUserId +
                File.separator +
                "Notes" +
                File.separator +
                uniqueDocumentId
    }

}