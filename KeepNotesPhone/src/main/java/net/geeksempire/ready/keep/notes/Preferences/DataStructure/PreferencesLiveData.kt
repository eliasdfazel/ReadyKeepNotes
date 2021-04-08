/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 9/21/20 12:13 PM
 * Last modified 9/21/20 11:31 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Preferences.DataStructure

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PreferencesLiveData : ViewModel() {

    /**
     * True To Force Reset Theme
     **/
    val toggleTheme: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }



}