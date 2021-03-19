package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.util.Log
import android.view.View
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesTemporaryModification
import net.geeksempire.ready.keep.notes.Database.IO.DeletingProcess
import net.geeksempire.ready.keep.notes.Invitations.Utils.ShareIt
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.UnderlayOptionsActions
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.UnpinnedRecyclerViewItemSwipeHelper

fun OverviewAdapterUnpinned.addItemToFirst(notesDatabaseModel: NotesDatabaseModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    if (this@addItemToFirst.notesDataStructureList[0].uniqueNoteId != notesDatabaseModel.uniqueNoteId) {

        this@addItemToFirst.notesDataStructureList.add(0, notesDatabaseModel)

        withContext(Dispatchers.Main) {

            this@addItemToFirst.notifyItemInserted(0)

        }

    }

}

fun OverviewAdapterUnpinned.rearrangeItemsData(fromPosition: Int, toPosition: Int) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    val selectedItem = notesDataStructureList[fromPosition]
    notesDataStructureList.removeAt(fromPosition)

    if (toPosition < fromPosition) {

        notesDataStructureList.add(toPosition, selectedItem)

    } else {

        notesDataStructureList.add(toPosition - 1, selectedItem)

    }

}

fun OverviewAdapterUnpinned.setupDeleteView(position: Int): UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton {

    return UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupDeleteView.context,
        this@setupDeleteView.context.getString(R.string.deleteText),
        13.0f,
        when (this@setupDeleteView.context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {
                R.color.white
            }
            ThemeType.ThemeDark -> {
                R.color.black
            } else -> {
                R.color.white
            }
        },
        R.color.red,
        object : UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).launch {

                val dataToDelete = context.overviewAdapterUnpinned.notesDataStructureList[position]

                DeletingProcess(context)
                    .start(dataToDelete, position)

                Log.d(this@setupDeleteView.javaClass.simpleName, "Note ${this@setupDeleteView.notesDataStructureList[position].uniqueNoteId} Deleted")
            }

        })
}

fun OverviewAdapterUnpinned.setupPinnedView(position: Int): UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton {

    return UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupPinnedView.context,
        this@setupPinnedView.context.getString(R.string.pinText),
        13.0f,
        when (this@setupPinnedView.context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {
                R.color.white
            }
            ThemeType.ThemeDark -> {
                R.color.black
            } else -> {
                R.color.white
            }
        },
        R.color.default_color_light,
        object : UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                val notesDatabaseDataAccessObject = (context.application as KeepNoteApplication)
                    .notesRoomDatabaseConfiguration
                    .prepareRead()

                if (this@setupPinnedView.notesDataStructureList[position].notePinned == NotesTemporaryModification.NotePinned) {

                    notesDatabaseDataAccessObject.updateNotePinnedData(this@setupPinnedView.notesDataStructureList[position].uniqueNoteId, NotesTemporaryModification.NoteUnpinned)

                    Firebase.auth.currentUser?.let { firebaseUser ->

                        if (!firebaseUser.isAnonymous) {

                            (context.application as KeepNoteApplication).firestoreDatabase
                                .document(context.databaseEndpoints.baseSpecificNoteEndpoint(firebaseUser.uid, this@setupPinnedView.notesDataStructureList[position].uniqueNoteId.toString()))
                                .update(
                                    Notes.NotePinned, NotesTemporaryModification.NoteUnpinned,
                                )

                        }

                    }

                } else {

                    notesDatabaseDataAccessObject.updateNotePinnedData(this@setupPinnedView.notesDataStructureList[position].uniqueNoteId, NotesTemporaryModification.NotePinned)

                    Firebase.auth.currentUser?.let { firebaseUser ->

                        if (!firebaseUser.isAnonymous) {

                            (context.application as KeepNoteApplication).firestoreDatabase
                                .document(context.databaseEndpoints.baseSpecificNoteEndpoint(firebaseUser.uid, this@setupPinnedView.notesDataStructureList[position].uniqueNoteId.toString()))
                                .update(
                                    Notes.NotePinned, NotesTemporaryModification.NotePinned,
                                )

                        }

                    }

                    delay(123)

                    val pinnedItem = this@setupPinnedView.notesDataStructureList.removeAt(position)

                    this@setupPinnedView.notifyItemRemoved(position)

                    if (context.overviewAdapterPinned.notesDataStructureList.isEmpty()) {

                        context.overviewAdapterPinned.notesDataStructureList.add(pinnedItem)

                        if (!context.overviewLayoutBinding.overviewPinnedRecyclerView.isShown) {
                            context.overviewLayoutBinding.overviewPinnedRecyclerView.visibility = View.VISIBLE
                        }

                        context.overviewAdapterPinned.notifyDataSetChanged()

                    } else {

                        context.overviewAdapterPinned.notesDataStructureList.add(0, pinnedItem)

                        context.overviewAdapterPinned.notifyItemInserted(0)

                    }

                }

                Log.d(this@setupPinnedView.javaClass.simpleName, "Note ${this@setupPinnedView.notesDataStructureList[position].uniqueNoteId} Pinned")
            }

        })
}

fun OverviewAdapterUnpinned.setupShareView(position: Int): UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton {

    return UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupShareView.context,
        this@setupShareView.context.getString(R.string.shareText),
        13.0f,
        when (this@setupShareView.context.themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {
                R.color.white
            }
            ThemeType.ThemeDark -> {
                R.color.black
            } else -> {
                R.color.white
            }
        },
        R.color.default_color_light,
        object : UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                val textShareStringBuilder = StringBuilder()
                notesDataStructureList[position].noteTile?.let {
                    textShareStringBuilder.append(context.contentEncryption.decryptEncodedData(it, Firebase.auth.currentUser!!.uid))
                }
                textShareStringBuilder.append("\n")
                notesDataStructureList[position].noteTextContent?.let {
                    textShareStringBuilder.append(context.contentEncryption.decryptEncodedData(it, Firebase.auth.currentUser!!.uid))
                }

                val textToShare = textShareStringBuilder.toString()

                val imageToShare = notesDataStructureList[position].noteHandwritingSnapshotLink

                val audioToShare = notesDataStructureList[position].noteVoicePaths

                ShareIt(context)
                    .invokeCompleteSharing(
                        textToShare,
                        imageToShare,
                        audioToShare
                    )

            }

        })
}

fun OverviewAdapterUnpinned.setupEditView(position: Int): UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton {

    return UnpinnedRecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupEditView.context,
        this@setupEditView.context.getString(R.string.editText),
        13.0f,
        R.color.white,
        R.color.default_color_light,
        object : UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

                if (paintingPathsJsonArray.isNullOrEmpty()) {

                    TakeNote.open(context = context,
                        incomingActivityName = this@setupEditView.javaClass.simpleName,
                        extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                        uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                        noteTile = notesDataStructureList[position].noteTile,
                        contentText = notesDataStructureList[position].noteTextContent,
                        encryptedTextContent = true,
                        updateExistingNote = true
                    )

                } else {

                    TakeNote.open(context = context,
                        incomingActivityName = this@setupEditView.javaClass.simpleName,
                        extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                        uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                        noteTile = notesDataStructureList[position].noteTile,
                        contentText = notesDataStructureList[position].noteTextContent,
                        paintingPath = paintingPathsJsonArray,
                        encryptedTextContent = true,
                        updateExistingNote = true
                    )

                }

            }

        })
}