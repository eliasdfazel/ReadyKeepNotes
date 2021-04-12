/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructureSearch
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption

class SearchViewModel : ViewModel() {

    val searchPreparedData: MutableLiveData<List<NotesDataStructureSearch>> by lazy {
        MutableLiveData<List<NotesDataStructureSearch>>()
    }

    val searchResults: MutableLiveData<List<NotesDataStructureSearch>> by lazy {
        MutableLiveData<List<NotesDataStructureSearch>>()
    }

    fun prepareDataForSearch(contentEncryption: ContentEncryption, firebaseUserUniqueId: String, allNotes: List<NotesDatabaseModel>) = CoroutineScope(Dispatchers.IO).async {

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
                    noteTextContent = contentEncryption.decryptEncodedData(it.noteTextContent.toString(), firebaseUserUniqueId).toString(),
                    noteHandwritingPaintingPaths = it.noteHandwritingPaintingPaths,
                    noteHandwritingSnapshotLink = it.noteHandwritingSnapshotLink,
                    noteTags = noteTags,
                    noteTranscribeTags = noteTranscribeTags
                ))

            }

        }

        searchPreparedData.postValue(plainNotesData)

    }

    fun searchInDatabase(searchTerm: String, allNotesData: List<NotesDataStructureSearch>) = CoroutineScope(Dispatchers.IO).launch {

        val allSearchResults = ArrayList<NotesDataStructureSearch>()

        allNotesData.asFlow()
            .filter {


                it.noteTile.contains(searchTerm) || it.noteTextContent.contains(searchTerm) || it.noteTags.contains(searchTerm) || it.noteTranscribeTags.contains(searchTerm)
            }
            .collect { searchResult ->
                Log.d(this@SearchViewModel.javaClass.simpleName, searchResult.toString())

                allSearchResults.add(searchResult)
            }

        searchResults.postValue(allSearchResults)

    }

}