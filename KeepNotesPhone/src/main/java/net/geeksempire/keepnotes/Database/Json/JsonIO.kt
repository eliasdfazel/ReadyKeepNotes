/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/25/20 9:50 PM
 * Last modified 7/25/20 9:47 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.Database.Json

import com.google.gson.Gson

class JsonIO {

    val gson: Gson = JsonConfiguration().initialize()

    fun writeParagraphToJson(jsonDataType: String, jsonDataContent: String) {



    }

}