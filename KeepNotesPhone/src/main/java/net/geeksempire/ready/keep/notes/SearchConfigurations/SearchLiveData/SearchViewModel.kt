package net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

class SearchViewModel : ViewModel() {

    val searchResults: MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }



}