package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R

fun KeepNoteOverview.startDatabaseOperation() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    if (getDatabasePath("NotesDatabase").exists()) {

        val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration
            .prepareRead()

        val allUnpinnedNotesData = notesDatabaseDataAccessObject.getAllPinnedNotesData(NotesTemporaryModification.NoteUnpinned)

        databaseSize = notesDatabaseDataAccessObject.getSizeOfDatabase().toLong()
        databaseTime = noteDatabaseConfigurations.lastTimeDatabaseUpdate()

        noteDatabaseConfigurations.databaseSize(databaseSize)

        notesOverviewViewModel.notesDatabaseUnpinned.postValue(allUnpinnedNotesData)

        val allPinnedNotesData = notesDatabaseDataAccessObject.getAllPinnedNotesData(NotesTemporaryModification.NotePinned)

        notesOverviewViewModel.notesDatabasePinned.postValue(allPinnedNotesData)

        notesRoomDatabaseConfiguration.closeDatabase()

    }

}

fun KeepNoteOverview.databaseOperationsCheckpoint() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

    val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

    val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration.prepareRead()

    val newDatabaseSize = notesDatabaseDataAccessObject
        .getSizeOfDatabase().toLong()

    noteDatabaseConfigurations.databaseSize(newDatabaseSize)

    if (overviewAdapterUnpinned.notesDataStructureList.isEmpty()) {

        startDatabaseOperation()

    } else {

        if (noteDatabaseConfigurations.lastTimeDatabaseUpdate() > databaseTime) {
            Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "Database Changed")

            if (newDatabaseSize == databaseSize) {
                Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "A Note Updated")

                val updatedDatabaseItemIdentifier = noteDatabaseConfigurations.updatedDatabaseItemIdentifier()
                val updatedDatabaseItemPosition = noteDatabaseConfigurations.updatedDatabaseItemPosition()

                val updatedNoteData = notesDatabaseDataAccessObject.getSpecificNoteData(updatedDatabaseItemIdentifier)

                when (updatedNoteData?.notePinned) {
                    NotesTemporaryModification.NotePinned -> {

                        val currentNoteData = overviewAdapterPinned.notesDataStructureList[updatedDatabaseItemPosition]

                        if (updatedNoteData.noteEditTime?:0.toLong() > currentNoteData.noteEditTime?:0.toLong()) {

                            overviewAdapterPinned.notesDataStructureList[updatedDatabaseItemPosition] = updatedNoteData

                            withContext(Dispatchers.Main) {

                                overviewAdapterPinned.notifyItemChanged(updatedDatabaseItemPosition)

                            }

                        }

                    }
                    NotesTemporaryModification.NoteUnpinned -> {

                        val currentNoteData = overviewAdapterUnpinned.notesDataStructureList[updatedDatabaseItemPosition]

                        if (updatedNoteData.noteEditTime?:0.toLong() > currentNoteData.noteEditTime?:0.toLong()) {

                            overviewAdapterUnpinned.notesDataStructureList[updatedDatabaseItemPosition] = updatedNoteData

                            withContext(Dispatchers.Main) {

                                overviewAdapterUnpinned.notifyItemChanged(updatedDatabaseItemPosition)

                            }

                        }

                    }
                }

            } else if (newDatabaseSize > databaseSize) {
                Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "New Note Added Into Database")

                val newestDatabaseItem: NotesDatabaseModel = notesDatabaseDataAccessObject.getNewestInsertedData()

                if (overviewAdapterUnpinned.notesDataStructureList[overviewAdapterUnpinned.itemCount - 1].uniqueNoteId != newestDatabaseItem.uniqueNoteId) {

                    overviewAdapterUnpinned.notesDataStructureList.add(0, newestDatabaseItem)

                    withContext(Dispatchers.Main) {

                        overviewAdapterUnpinned.notifyItemInserted(0)

                    }
                }

            }

        }

    }

    notesRoomDatabaseConfiguration.closeDatabase()

}

fun KeepNoteOverview.startNetworkOperation() {

    Firebase.auth.currentUser?.let { firebaseUser ->

        Handler(Looper.getMainLooper()).postDelayed({

            if (firebaseUser.email.isNullOrBlank()) {

                overviewLayoutBinding.notSynchronizing.visibility = View.VISIBLE
                overviewLayoutBinding.notSynchronizing.playAnimation()

            }

        }, 555)

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