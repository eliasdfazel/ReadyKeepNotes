package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import android.app.ActivityOptions
import android.content.Intent
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


                    context.startActivity(Intent(context, TakeNote::class.java).apply {
                        putExtra(TakeNote.NoteExtraData.DocumentId, notesDataStructureList[position].uniqueNoteId)
                        putExtra(TakeNote.NoteExtraData.TitleText, notesDataStructureList[position].noteTile)
                        putExtra(TakeNote.NoteExtraData.ContentText, notesDataStructureList[position].noteTextContent)
                    }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                } else {


                    context.startActivity(Intent(context, TakeNote::class.java).apply {
                        putExtra(TakeNote.NoteExtraData.DocumentId, notesDataStructureList[position].uniqueNoteId)
                        putExtra(TakeNote.NoteExtraData.TitleText, notesDataStructureList[position].noteTile.toString())
                        putExtra(TakeNote.NoteExtraData.ContentText, notesDataStructureList[position].noteTextContent.toString())
                        putExtra(TakeNote.NoteExtraData.PaintingPath, paintingPathsJsonArray)
                    }, ActivityOptions.makeCustomAnimation(context, R.anim.fade_in, 0).toBundle())

                }

            }

        })
}