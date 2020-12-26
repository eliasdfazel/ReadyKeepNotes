/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/25/20 9:50 PM
 * Last modified 7/25/20 9:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.Json

import com.google.gson.Gson
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.RedrawPaintingData

class JsonIO {

    private val jsonDatabase: Gson = JsonConfiguration().initialize()

    fun writePaintingPathData(aPathXY: ArrayList<RedrawPaintingData>) : String {

        return jsonDatabase.toJson(aPathXY).toString()
    }

    fun writeTagsLineSeparated(allTags: String) : String {

        return jsonDatabase.toJson(allTags).toString()
    }

}