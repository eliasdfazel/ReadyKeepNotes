/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
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