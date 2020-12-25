package net.geeksempire.ready.keep.notes.Database.DataStructure

import com.google.firebase.firestore.FieldValue

object Notes {
    const val NoteTile: String = "noteTile"
    const val NoteTextContent: String = "noteTextContent"
    const val NoteHandwritingSnapshotLink: String = "noteHandwritingSnapshotLink"
    const val NoteTakenTime: String = "noteTakenTime"
    const val NoteEditTime: String = "noteEditTime"
    const val NoteIndex: String = "noteIndex"
}

data class NotesDataStructure(var noteTile: String = "Untitled Note",
                              var noteTextContent: String = "No Content",
                              var noteHandwritingSnapshotLink: String? = null,
                              var noteTakenTime: FieldValue = FieldValue.serverTimestamp(),
                              var noteEditTime: FieldValue? = null,
                              var noteIndex: Long)