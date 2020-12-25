package net.geeksempire.ready.keep.notes.Overview.NotesLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class NotesOverviewViewModel : ViewModel() {

    val notesQuerySnapshots : MutableLiveData<ArrayList<DocumentSnapshot>> by lazy {
        MutableLiveData<ArrayList<DocumentSnapshot>>()
    }

    fun processDocumentSnapshots(documentSnapshotsList: List<DocumentSnapshot>) = CoroutineScope(Dispatchers.IO).async {

        notesQuerySnapshots.postValue(documentSnapshotsList as ArrayList<DocumentSnapshot>?)

    }

}