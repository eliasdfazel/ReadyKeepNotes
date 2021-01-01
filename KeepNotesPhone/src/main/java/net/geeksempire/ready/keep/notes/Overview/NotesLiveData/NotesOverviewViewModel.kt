package net.geeksempire.ready.keep.notes.Overview.NotesLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

class NotesOverviewViewModel : ViewModel() {

    val notesDatabaseQuerySnapshots : MutableLiveData<ArrayList<NotesDatabaseModel>> by lazy {
        MutableLiveData<ArrayList<NotesDatabaseModel>>()
    }

    val notesFirestoreQuerySnapshots : MutableLiveData<ArrayList<DocumentSnapshot>> by lazy {
        MutableLiveData<ArrayList<DocumentSnapshot>>()
    }

    fun processDocumentSnapshots(documentSnapshotsList: List<DocumentSnapshot>) = CoroutineScope(Dispatchers.IO).async {

        notesFirestoreQuerySnapshots.postValue(documentSnapshotsList as ArrayList<DocumentSnapshot>?)

    }

}