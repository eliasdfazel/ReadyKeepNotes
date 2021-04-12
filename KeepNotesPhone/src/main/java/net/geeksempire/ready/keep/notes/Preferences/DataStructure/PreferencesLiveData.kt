/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
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