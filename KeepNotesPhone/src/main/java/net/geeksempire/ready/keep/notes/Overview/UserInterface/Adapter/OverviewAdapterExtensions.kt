package net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter

import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

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