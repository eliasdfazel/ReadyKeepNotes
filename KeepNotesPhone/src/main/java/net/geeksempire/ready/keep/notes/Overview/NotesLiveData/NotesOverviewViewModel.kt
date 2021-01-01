package net.geeksempire.ready.keep.notes.Overview.NotesLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel

class NotesOverviewViewModel : ViewModel() {

    val notesDatabaseQuerySnapshots : MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

    fun processDocumentSnapshots(documentSnapshotsList: List<DocumentSnapshot>) = CoroutineScope(Dispatchers.IO).async {

        //Download & Save To Room Database
        documentSnapshotsList.forEach {



        }

    }

}