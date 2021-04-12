/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Data

import android.content.Context
import net.geeksempire.ready.keep.notes.BuildConfig

class LastUpdateInformation (private val context: Context){

    fun isApplicationUpdated() : Boolean{

        val fileIO: FileIO = FileIO(context)

        return (BuildConfig.VERSION_CODE > fileIO.readFile(".Updated")?.toInt()?:0)
    }
}