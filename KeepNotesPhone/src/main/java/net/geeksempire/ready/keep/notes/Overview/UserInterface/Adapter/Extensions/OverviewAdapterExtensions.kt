package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Invitations.Utils.ShareIt
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.RecyclerViewItemSwipeHelper

fun OverviewAdapter.addItemToFirst(notesDatabaseModel: NotesDatabaseModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    if (this@addItemToFirst.notesDataStructureList[0].uniqueNoteId != notesDatabaseModel.uniqueNoteId) {

        this@addItemToFirst.notesDataStructureList.add(0, notesDatabaseModel)

        withContext(Dispatchers.Main) {

            this@addItemToFirst.notifyItemInserted(0)

        }

    }

}

fun OverviewAdapter.rearrangeItemsData(fromPosition: Int, toPosition: Int) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    val selectedItem = notesDataStructureList[fromPosition]
    notesDataStructureList.removeAt(fromPosition)

    if (toPosition < fromPosition) {

        notesDataStructureList.add(toPosition, selectedItem)

    } else {

        notesDataStructureList.add(toPosition - 1, selectedItem)

    }

}

fun OverviewAdapter.setupDeleteView(position: Int): RecyclerViewItemSwipeHelper.UnderlayButton {

    return RecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupDeleteView.context,
        this@setupDeleteView.context.getString(R.string.deleteText),
        17.0f,
        R.color.red,
        object : RecyclerViewItemSwipeHelper.UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                val dataToDelete = context.overviewAdapter.notesDataStructureList[position]

                context.overviewAdapter.notesDataStructureList.removeAt(position)
                context.overviewAdapter.notifyItemRemoved(position)

                val notesRoomDatabaseConfiguration = (this@setupDeleteView.context.application as KeepNoteApplication).notesRoomDatabaseConfiguration

                notesRoomDatabaseConfiguration
                    .prepareRead()
                    .deleteNoteData(dataToDelete)

                notesRoomDatabaseConfiguration.closeDatabase()

            }

        })
}

fun OverviewAdapter.setupEditView(position: Int): RecyclerViewItemSwipeHelper.UnderlayButton {

    return RecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupEditView.context,
        this@setupEditView.context.getString(R.string.editText),
        17.0f,
        R.color.default_color,
        object : RecyclerViewItemSwipeHelper.UnderlayOptionsActions {

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

fun OverviewAdapter.setupShareView(position: Int): RecyclerViewItemSwipeHelper.UnderlayButton {

    return RecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupShareView.context,
        this@setupShareView.context.getString(R.string.shareText),
        17.0f,
        R.color.cyan,
        object : RecyclerViewItemSwipeHelper.UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                if (!notesDataStructureList[position].noteHandwritingSnapshotLink.isNullOrBlank()) {

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

                    ShareIt(context)
                        .invokeCompleteSharing(
                            textToShare,
                            imageToShare
                        )

                } else {

                    val textShareStringBuilder = StringBuilder()
                    notesDataStructureList[position].noteTile?.let {
                        textShareStringBuilder.append(context.contentEncryption.decryptEncodedData(it, Firebase.auth.currentUser!!.uid))
                    }
                    textShareStringBuilder.append("\n")
                    notesDataStructureList[position].noteTextContent?.let {
                        textShareStringBuilder.append(context.contentEncryption.decryptEncodedData(it, Firebase.auth.currentUser!!.uid))
                    }

                    val textToShare = textShareStringBuilder.toString()

                    ShareIt(context)
                        .invokeCompleteSharing(
                            textToShare,
                            null
                        )

                }

            }

        })
}