package net.geeksempire.ready.keep.notes.Overview.NotesLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

class NotesOverviewViewModel : ViewModel() {

    val notesDatabaseUnpinned : MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

    val notesDatabasePinned : MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

}