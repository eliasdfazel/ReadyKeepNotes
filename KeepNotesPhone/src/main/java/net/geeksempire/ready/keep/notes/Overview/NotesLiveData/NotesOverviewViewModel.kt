/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

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