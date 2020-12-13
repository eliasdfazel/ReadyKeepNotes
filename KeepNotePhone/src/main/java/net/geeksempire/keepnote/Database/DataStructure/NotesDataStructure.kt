package net.geeksempire.keepnote.Database.DataStructure

import com.google.firebase.firestore.FieldValue

data class NotesDataStructure(var noteTile: String = "Untitled",
                              var noteTextContent: String? = null,
                              var noteHandwritingSnapshotLink: String? = null,
                              var noteTakenTime: FieldValue = FieldValue.serverTimestamp(),
                              var noteEditTime: FieldValue? = null)