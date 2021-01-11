package net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructureSearch
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption

class SearchViewModel : ViewModel() {

    val searchResults: MutableLiveData<List<NotesDatabaseModel>> by lazy {
        MutableLiveData<List<NotesDatabaseModel>>()
    }

    fun prepareDataForSearch(contentEncryption: ContentEncryption, firebaseUserUniqueId: String, allNotes: List<NotesDatabaseModel>) : Deferred<List<NotesDataStructureSearch>> = CoroutineScope(Dispatchers.IO).async {

        val plainNotesData = ArrayList<NotesDataStructureSearch>()

        allNotes.forEach {

            val noteTitle = it.noteTile.toString()
            val noteTextContent = it.noteTextContent.toString()
            val noteTags = it.noteTags.toString()
            val noteTranscribeTags = it.noteTranscribeTags.toString()

            if (noteTitle.isNotBlank() || noteTextContent.isNotBlank() || noteTags.isNotBlank() || noteTranscribeTags.isNotBlank()) {

                plainNotesData.add(NotesDataStructureSearch(
                    uniqueNoteId = it.uniqueNoteId,
                    noteTile = contentEncryption.decryptEncodedData(it.noteTile.toString(), firebaseUserUniqueId).toString(),
                    noteTextContent = it.noteTextContent.toString(),
                    noteHandwritingPaintingPaths = it.noteHandwritingPaintingPaths,
                    noteHandwritingSnapshotLink = it.noteHandwritingSnapshotLink,
                    noteTags = noteTags,
                    noteTranscribeTags = noteTranscribeTags
                ))

            }

        }

        plainNotesData
    }

    fun searchInDatabase(searchTerm: String, allNotesData: List<NotesDataStructureSearch>) = CoroutineScope(Dispatchers.IO).async {

        allNotesData.filter {

            it.noteTile.contains(searchTerm) || it.noteTextContent.contains(searchTerm) || it.noteTags.contains(searchTerm) || it.noteTranscribeTags.contains(searchTerm)
        }

    }

}