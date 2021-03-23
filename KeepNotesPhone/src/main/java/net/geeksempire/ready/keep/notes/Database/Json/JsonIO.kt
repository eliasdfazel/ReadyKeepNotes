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
import net.geeksempire.ready.keep.notes.ContentContexts.DataStructure.TagsData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.RedrawPaintingData
import net.geeksempire.ready.keep.notes.ReminderConfigurations.DataStructure.ReminderDataStructure

class JsonIO {

    private val jsonDatabase: Gson = JsonConfiguration().initialize()

    /**
     * Json Array
     **/
    fun writeAllPaintingPathData(allPathXY: ArrayList<ArrayList<RedrawPaintingData>>) : String {

        return jsonDatabase.toJson(allPathXY).toString()
    }

    /**
     * Json Array
     **/
    fun writePaintingPathData(aPathXY: ArrayList<RedrawPaintingData>) : String {

        return jsonDatabase.toJson(aPathXY).toString()
    }

    /**
     * Json Array
     **/
    fun writeTagsData(allTags: ArrayList<TagsData>) : String {

        return jsonDatabase.toJson(allTags).toString()
    }

    /**
     * Json Array
     **/
    fun writeAudioRecordingFilePaths(allAudioRecordingFile: ArrayList<String>) : String {

        return jsonDatabase.toJson(allAudioRecordingFile).toString()

    }

    /**
     * Json Object
     **/
    fun writeReminderData(reminderDataStructure: ReminderDataStructure) : String {

        return jsonDatabase.toJson(reminderDataStructure)
    }

}