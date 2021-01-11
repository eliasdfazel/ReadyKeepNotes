package net.geeksempire.ready.keep.notes.Database.DataStructure

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Keep
object Notes {
    const val NoteTile = "noteTile"
    const val NoteTextContent = "noteTextContent"

    const val NoteHandwritingSnapshotLink = "noteHandwritingSnapshotLink"
    const val noteHandwritingPaintingPaths = "noteHandwritingPaintingPaths"

    const val noteVoiceContent = "noteVoiceContent"
    const val noteImageContent = "noteImageContent"
    const val noteGifContent = "noteGifContent"

    const val NoteTakenTime = "noteTakenTime"
    const val NoteEditTime = "noteEditTime"

    const val NoteIndex = "noteIndex"

    const val NotesTags = "noteTags"
}

@Keep
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

@Keep
data class NotesDataStructureSearch(var uniqueNoteId: Long,
                                    var noteTile: String,
                                    var noteTextContent: String,
                                    var noteHandwritingPaintingPaths: String? = null,
                                    var noteHandwritingSnapshotLink: String? = null,
                                    var noteTags: String,
                                    var noteTranscribeTags: String)

const val NotesDatabase = "NotesDatabase"

@Entity(tableName = NotesDatabase)
@Keep
data class NotesDatabaseModel(
    @NonNull @PrimaryKey var uniqueNoteId: Long,

    @Nullable @ColumnInfo(name = "noteTile", typeAffinity = ColumnInfo.TEXT) var noteTile: String? = null,
    @Nullable @ColumnInfo(name = "noteTextContent", typeAffinity = ColumnInfo.TEXT) var noteTextContent: String? = null,

    @Nullable @ColumnInfo(name = "noteHandwritingPaintingPaths", typeAffinity = ColumnInfo.TEXT) var noteHandwritingPaintingPaths: String?,
    @Nullable @ColumnInfo(name = "noteHandwritingSnapshotLink", typeAffinity = ColumnInfo.TEXT) var noteHandwritingSnapshotLink: String?,

    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteVoicePaths", typeAffinity = ColumnInfo.TEXT) var noteVoiceContent: String? = null,
    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteImagePaths", typeAffinity = ColumnInfo.TEXT) var noteImageContent: String? = null,
    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteGifPaths", typeAffinity = ColumnInfo.TEXT) var noteGifContent: String? = null,

    @NonNull @ColumnInfo(name = "noteTakenTime", typeAffinity = ColumnInfo.INTEGER) var noteTakenTime: Long,
    @Nullable @ColumnInfo(name = "noteEditTime", typeAffinity = ColumnInfo.INTEGER) var noteEditTime: Long? = null,

    @NonNull @ColumnInfo(name = "noteIndex") var noteIndex: Long,

    /**
     * Json Of Tags
     **/
    @Nullable @ColumnInfo(name = "noteTags", typeAffinity = ColumnInfo.TEXT) var noteTags: String? = null,
    /**
     * Json Of Hash Tags
     **/
    @Nullable @ColumnInfo(name = "noteHashTags", typeAffinity = ColumnInfo.TEXT) var noteHashTags: String? = null,
    /**
     * Json Of Transcribe Tags
     **/
    @Nullable @ColumnInfo(name = "noteTranscribeTags", typeAffinity = ColumnInfo.TEXT) var noteTranscribeTags: String? = null,

    @NonNull @ColumnInfo(name = "dataSelected") var dataSelected: Int = NotesTemporaryModification.NoteIsNotSelected
)