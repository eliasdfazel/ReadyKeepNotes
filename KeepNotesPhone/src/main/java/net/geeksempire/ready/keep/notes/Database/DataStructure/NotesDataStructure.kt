package net.geeksempire.ready.keep.notes.Database.DataStructure

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

object Notes {
    const val NoteTile: String = "noteTile"
    const val NoteTextContent: String = "noteTextContent"
    const val NoteHandwritingSnapshotLink: String = "noteHandwritingSnapshotLink"
    const val noteHandwritingPaintingPaths: String = "noteHandwritingPaintingPaths"
    const val NoteTakenTime: String = "noteTakenTime"
    const val NoteEditTime: String = "noteEditTime"
    const val NoteIndex: String = "noteIndex"
    const val NotesTags: String = "noteTags"
}

object NotesTemporaryModification {
    const val NoteIsNotSelected = 0
    const val NoteIsSelected = 1
}

@Keep
data class NotesDataStructure(var uniqueNoteId: Long,
                              var noteTile: String = "Untitled Note",
                              var noteTextContent: String = "No Content",
                              var noteHandwritingPaintingPaths: String? = null,
                              var noteHandwritingSnapshotLink: String? = null,
                              var noteTakenTime: Timestamp = Timestamp.now(),
                              var noteEditTime: Timestamp? = null,
                              var noteIndex: Long,
                              var noteTags: String? = null)

const val NotesDatabase = "NotesDatabase"

@Keep
@Entity(tableName = NotesDatabase)
data class NotesDatabaseModel(
    @NonNull @PrimaryKey var uniqueNoteId: Long,

    @Nullable @ColumnInfo(name = "noteTile", typeAffinity = ColumnInfo.TEXT) var noteTile: String?,
    @Nullable @ColumnInfo(name = "noteTextContent", typeAffinity = ColumnInfo.TEXT) var noteTextContent: String?,
    @Nullable @ColumnInfo(name = "noteHandwritingPaintingPaths", typeAffinity = ColumnInfo.TEXT) var noteHandwritingPaintingPaths: String?,
    @Nullable @ColumnInfo(name = "noteHandwritingSnapshotLink", typeAffinity = ColumnInfo.TEXT) var noteHandwritingSnapshotLink: String?,

    @NonNull @ColumnInfo(name = "noteTakenTime") var noteTakenTime: Long,
    @Nullable @ColumnInfo(name = "noteEditTime") var noteEditTime: Long?,

    @NonNull @ColumnInfo(name = "noteIndex") var noteIndex: Long,

    @Nullable @ColumnInfo(name = "noteTags", typeAffinity = ColumnInfo.TEXT) var noteTags: String?,
    @NonNull @ColumnInfo(name = "dataSelected") var dataSelected: Int = 0
)