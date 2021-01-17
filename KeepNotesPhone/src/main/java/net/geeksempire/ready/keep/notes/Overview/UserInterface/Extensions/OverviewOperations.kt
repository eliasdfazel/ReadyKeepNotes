package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R

fun KeepNoteOverview.startDatabaseOperation() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    if (getDatabasePath("NotesDatabase").exists()) {

        val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

        val allNotesData = notesRoomDatabaseConfiguration
            .prepareRead()
            .getAllNotesData()

        databaseSize = allNotesData.size.toLong()
        databaseTime = noteDatabaseConfigurations.lastTimeDatabaseUpdate()

        noteDatabaseConfigurations.databaseSize(databaseSize)

        notesOverviewViewModel.notesDatabaseQuerySnapshots.postValue(allNotesData)

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}

fun KeepNoteOverview.databaseOperationsCheckpoint() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

    val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

    val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration.prepareRead()

    val newDatabaseSize = notesDatabaseDataAccessObject
        .getSizeOfDatabase().toLong()

    noteDatabaseConfigurations.databaseSize(newDatabaseSize)

    if (overviewAdapter.notesDataStructureList.isEmpty()) {

        startDatabaseOperation()

    } else {

        if (noteDatabaseConfigurations.lastTimeDatabaseUpdate() > databaseTime) {
            Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "Database Changed")

            if (newDatabaseSize == databaseSize) {
                Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "A Note Updated")

                val updatedDatabaseItemIdentifier = noteDatabaseConfigurations.updatedDatabaseItemIdentifier()
                val updatedDatabaseItemPosition = noteDatabaseConfigurations.updatedDatabaseItemPosition()

                val updatedNoteData = notesDatabaseDataAccessObject.getSpecificNoteData(updatedDatabaseItemIdentifier)

                val currentNoteData = overviewAdapter.notesDataStructureList[updatedDatabaseItemPosition]

                if (updatedNoteData!!.noteEditTime?:0.toLong() > currentNoteData.noteEditTime?:0.toLong()) {

                    overviewAdapter.notesDataStructureList[updatedDatabaseItemPosition] = updatedNoteData

                    withContext(Dispatchers.Main) {

                        overviewAdapter.notifyItemChanged(updatedDatabaseItemPosition)

                    }

                }

            } else if (newDatabaseSize > databaseSize) {
                Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "New Note Added Into Database")

                val newestDatabaseItem: NotesDatabaseModel = notesDatabaseDataAccessObject.getNewestInsertedData()

                if (overviewAdapter.notesDataStructureList[overviewAdapter.itemCount - 1].uniqueNoteId != newestDatabaseItem.uniqueNoteId) {

                    overviewAdapter.notesDataStructureList.add(0, newestDatabaseItem)

                    withContext(Dispatchers.Main) {

                        overviewAdapter.notifyItemInserted(0)

                    }
                }

            }

        }

    }

    notesRoomDatabaseConfiguration.closeDatabase()

}

fun KeepNoteOverview.startNetworkOperation() {

    Firebase.auth.currentUser?.let {

        //

    }

}

fun KeepNoteOverview.loadUserAccountInformation() {

    Firebase.auth.currentUser?.let {

        Glide.with(applicationContext)
            .load(it.photoUrl)
            .error(getDrawable(R.drawable.not_login_icon))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(CircleCrop())
            .into(overviewLayoutBinding.profileImageView)

    }

}