/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 7/25/20 9:50 PM
 * Last modified 7/25/20 9:29 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.Json

import com.google.gson.Gson
import com.google.gson.GsonBuilder

class JsonConfiguration {

    fun initialize() : Gson {

        return GsonBuilder()
            .setPrettyPrinting()
            .create()
    }
}