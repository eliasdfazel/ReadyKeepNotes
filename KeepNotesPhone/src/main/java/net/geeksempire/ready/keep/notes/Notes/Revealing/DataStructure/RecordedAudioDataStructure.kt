package net.geeksempire.ready.keep.notes.Notes.Revealing.DataStructure

import java.io.File

data class RecordedAudioDataStructure (var audioFile: File, var audioFilePath: String, var audioFileName: String,
                                       var audioPlaying: Boolean = false)