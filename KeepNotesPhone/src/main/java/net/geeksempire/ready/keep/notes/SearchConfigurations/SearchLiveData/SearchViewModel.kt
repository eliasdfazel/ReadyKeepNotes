package net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.NotesDatabaseDataAccessObject

class SearchViewModel : ViewModel() {

    val searchResults: MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

    fun searchInDatabase(searchTerm: String, notesDatabaseDataAccessObject: NotesDatabaseDataAccessObject) = CoroutineScope(Dispatchers.IO).async {

        notesDatabaseDataAccessObject.searchAllNotesData(searchTerm).also {

            searchResults.postValue(it)
        }

    }

}