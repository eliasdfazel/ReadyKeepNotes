/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/8/20 1:39 AM
 * Last modified 8/8/20 1:35 AM
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