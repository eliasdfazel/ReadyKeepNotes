/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Notes.Revealing.DataStructure

import java.io.File

data class RecordedAudioDataStructure (var documentId: String, var audioRecordedId: String, var audioFile: File, var audioFilePath: String, var audioFileName: String,
                                       var audioPlaying: Boolean = false)