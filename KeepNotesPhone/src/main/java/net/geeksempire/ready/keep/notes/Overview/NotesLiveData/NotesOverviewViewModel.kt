package net.geeksempire.ready.keep.notes.Overview.NotesLiveData

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.IO.PaintingIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication

class NotesOverviewViewModel : ViewModel() {

    val notesDatabaseQuerySnapshots : MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

    fun downloadNotesData(context: AppCompatActivity, documentSnapshotsList: List<DocumentSnapshot>) {

        val paintingIO = PaintingIO(context)

        documentSnapshotsList.forEach { documentSnapshot ->

            (context.application as KeepNoteApplication)
                .firestoreDatabase.collection(DatabaseEndpoints().paintPathsCollectionEndpoints(documentSnapshot.reference.path))
                .get()
                .addOnSuccessListener { querySnapshot ->

                    CoroutineScope(Dispatchers.IO).async {

                        var paintingPaths: String? = null

                        if (!querySnapshot.isEmpty) {

                            paintingPaths = paintingIO.preparePaintingPathsOnline(querySnapshot.documents.toList())

                        }

                        (context.application as KeepNoteApplication)
                            .notesRoomDatabaseConfiguration
                            .insertNewNoteData(NotesDatabaseModel(
                                uniqueNoteId = documentSnapshot.id.toLong(),
                                noteTile = documentSnapshot[Notes.NoteTile].toString(),
                                noteTextContent = documentSnapshot[Notes.NoteTextContent].toString(),
                                noteHandwritingSnapshotLink = documentSnapshot[Notes.NoteHandwritingSnapshotLink].toString(),
                                noteTakenTime = documentSnapshot[Notes.NoteTakenTime].toString().toLong(),
                                noteHandwritingPaintingPaths = paintingPaths,
                                noteEditTime = null,
                                noteIndex = documentSnapshot[Notes.NoteIndex].toString().toLong(),
                                noteTags = null
                            ))

                    }

                }.addOnFailureListener {



                }

        }

    }

}