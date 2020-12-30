package net.geeksempire.ready.keep.notes.Database.DataStructure

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

const val NotesDatabase = "NotesDatabase"

@Entity(tableName = NotesDatabase)
data class NotesDatabaseModel(
    @NonNull @PrimaryKey var uniqueNoteId: Long,

    @Nullable @ColumnInfo(name = "noteTile") var noteTile: String?,
    @Nullable @ColumnInfo(name = "noteTextContent") var noteTextContent: String?,
    @Nullable @ColumnInfo(name = "noteHandwritingSnapshotLink") var noteHandwritingSnapshotLink: String?,

    @NonNull @ColumnInfo(name = "noteTakenTime") var noteTakenTime: Long,
    @Nullable @ColumnInfo(name = "noteEditTime") var noteEditTime: Long?,

    @NonNull @ColumnInfo(name = "noteIndex") var noteIndex: Long
)