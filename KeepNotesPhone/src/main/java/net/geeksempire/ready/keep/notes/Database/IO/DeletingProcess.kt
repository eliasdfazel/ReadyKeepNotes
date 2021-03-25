package net.geeksempire.ready.keep.notes.Database.IO

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Tools.Directory.BaseDirectory
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import java.io.File

class DeletingProcess (private val keepNoteApplication: KeepNoteOverview) {

    fun start(notesDatabaseModel: NotesDatabaseModel, selectedDataPosition: Int) = CoroutineScope(Dispatchers.IO).async {

        //Delete Data On Cloud
        Firebase.auth.currentUser?.let { firebaseUser ->

            if (!firebaseUser.isAnonymous) {

                (keepNoteApplication.application as KeepNoteApplication).firestoreDatabase
                    .document(DatabaseEndpoints().noteTextsDocumentEndpoint(firebaseUser.uid, notesDatabaseModel.uniqueNoteId.toString()))
                    .delete()

                (keepNoteApplication.application as KeepNoteApplication).firebaseStorage
                    .getReference(DatabaseEndpoints().baseSpecificNoteEndpoint(firebaseUser.uid, notesDatabaseModel.uniqueNoteId.toString()))
                    .delete()

            }

            //Delete Data On SdCard - Handwriting Snapshot
            //Delete Data On SdCard - Audio Records
            //Delete Data On SdCard - Images
            val filesDirectory = File(BaseDirectory().localBaseSpecificDirectory(keepNoteApplication, firebaseUser.uid, notesDatabaseModel.uniqueNoteId.toString()))

            if (filesDirectory.exists()) {

                filesDirectory.delete()

            }

        }

        //Delete Data On User Interface
        keepNoteApplication.overviewAdapterUnpinned.notesDataStructureList.removeAt(selectedDataPosition)
        keepNoteApplication.overviewAdapterUnpinned.notifyItemRemoved(selectedDataPosition)

        //Delete Data On Local Database
        val notesRoomDatabaseConfiguration = (keepNoteApplication.application as KeepNoteApplication).notesRoomDatabaseConfiguration

        notesRoomDatabaseConfiguration
            .prepareRead()
            .deleteNoteData(notesDatabaseModel)

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}