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
import org.json.JSONArray
import java.io.File

class DeletingProcess (private val keepNoteApplication: KeepNoteOverview) {

    fun start(notesDatabaseModel: NotesDatabaseModel, selectedDataPosition: Int) = CoroutineScope(Dispatchers.IO).async {

        //Delete Data On Cloud
        Firebase.auth.currentUser?.let { firebaseUser ->

            (keepNoteApplication.application as KeepNoteApplication).firestoreDatabase
                .document(DatabaseEndpoints().noteTextsDocumentEndpoint(firebaseUser.uid, notesDatabaseModel.uniqueNoteId.toString()))
                .delete()

        }

        //Delete Data On User Interface
        keepNoteApplication.overviewAdapter.notesDataStructureList.removeAt(selectedDataPosition)
        keepNoteApplication.overviewAdapter.notifyItemRemoved(selectedDataPosition)

        //Delete Data On Local Database
        val notesRoomDatabaseConfiguration = (keepNoteApplication.application as KeepNoteApplication).notesRoomDatabaseConfiguration

        notesRoomDatabaseConfiguration
            .prepareRead()
            .deleteNoteData(notesDatabaseModel)

        notesRoomDatabaseConfiguration.closeDatabase()

        //Delete Data On SdCard - Handwriting Snapshot
        notesDatabaseModel.noteHandwritingPaintingPaths?.let { handwritingSnapshotFileToDelete ->

            File(handwritingSnapshotFileToDelete).takeIf { it.exists() }?.delete()

        }

        //Delete Data On SdCard - Audio Records
        notesDatabaseModel.noteVoiceContent?.let { audioContent ->

            val jsonArrayAudios = JSONArray(audioContent)

            for (index in 0 until jsonArrayAudios.length()) {

                File(jsonArrayAudios.getJSONObject(index).getString("Path")).takeIf { it.exists() }?.delete()

            }

        }

    }

}