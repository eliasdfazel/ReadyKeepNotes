package net.geeksempire.ready.keep.notes.Database.IO

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import java.io.File

class DeletingProcess (private val keepNoteApplication: KeepNoteOverview) {

    fun start(notesDatabaseModel: NotesDatabaseModel, selectedDataPosition: Int) = CoroutineScope(Dispatchers.IO).async {

        Firebase.auth.currentUser?.let { firebaseUser ->

            (keepNoteApplication.application as KeepNoteApplication).firestoreDatabase
                .document(DatabaseEndpoints().noteTextsDocumentEndpoint(firebaseUser.uid, notesDatabaseModel.uniqueNoteId.toString()))
                .delete()

        }

        notesDatabaseModel.noteHandwritingPaintingPaths?.let { handwritingSnapshotFileToDelete ->

            File(handwritingSnapshotFileToDelete).delete()

        }

        keepNoteApplication.overviewAdapter.notesDataStructureList.removeAt(selectedDataPosition)
        keepNoteApplication.overviewAdapter.notifyItemRemoved(selectedDataPosition)

        val notesRoomDatabaseConfiguration = (keepNoteApplication.application as KeepNoteApplication).notesRoomDatabaseConfiguration

        notesRoomDatabaseConfiguration
            .prepareRead()
            .deleteNoteData(notesDatabaseModel)

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}