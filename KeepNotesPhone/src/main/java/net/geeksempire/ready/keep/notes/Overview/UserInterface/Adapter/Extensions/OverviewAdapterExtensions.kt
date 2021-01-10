package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.RecyclerViewItemSwipeHelper

fun OverviewAdapter.addItemToFirst(notesDatabaseModel: NotesDatabaseModel) = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    this@addItemToFirst.notesDataStructureList.add(notesDatabaseModel)

    withContext(Dispatchers.Main) {

        this@addItemToFirst.notifyItemInserted(this@addItemToFirst.notesDataStructureList.size)

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

                (this@setupDeleteView.context.application as KeepNoteApplication)
                    .notesRoomDatabaseConfiguration
                    .deleteNoteData(dataToDelete)

            }

        })
}

fun OverviewAdapter.setupShareView(position: Int): RecyclerViewItemSwipeHelper.UnderlayButton {

    return RecyclerViewItemSwipeHelper.UnderlayButton(
        this@setupShareView.context,
        this@setupShareView.context.getString(R.string.editText),
        17.0f,
        R.color.default_color,
        object : RecyclerViewItemSwipeHelper.UnderlayOptionsActions {

            override fun onClick() = CoroutineScope(Dispatchers.Main).async {

                val paintingPathsJsonArray = notesDataStructureList[position].noteHandwritingPaintingPaths

                if (paintingPathsJsonArray.isNullOrEmpty()) {

                    TakeNote.open(context = context,
                        incomingActivityName = this@setupShareView.javaClass.simpleName,
                        extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                        uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                        noteTile = notesDataStructureList[position].noteTile,
                        contentText = notesDataStructureList[position].noteTextContent,
                        encryptedTextContent = true
                    )

                } else {

                    TakeNote.open(context = context,
                        incomingActivityName = this@setupShareView.javaClass.simpleName,
                        extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
                        uniqueNoteId = notesDataStructureList[position].uniqueNoteId,
                        noteTile = notesDataStructureList[position].noteTile,
                        contentText = notesDataStructureList[position].noteTextContent,
                        paintingPath = paintingPathsJsonArray,
                        encryptedTextContent = true
                    )

                }

            }

        })
}