/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Network.Extensions

import android.util.Log
import org.json.JSONArray
import org.json.JSONObject

interface JsonRequestResponseInterface {
    fun jsonRequestResponseSuccessHandler(rawDataJsonArray: JSONArray) {
        Log.d(this@JsonRequestResponseInterface.javaClass.simpleName, rawDataJsonArray.toString())
    }

    fun jsonRequestResponseSuccessHandler(rawDataJsonObject: JSONObject) {
        Log.d(this@JsonRequestResponseInterface.javaClass.simpleName, rawDataJsonObject.toString())

    }

    fun jsonRequestResponseFailureHandler(jsonError: String?) {
        Log.d(this@JsonRequestResponseInterface.javaClass.simpleName, jsonError.toString())

    }

    fun jsonRequestResponseFailureHandler(networkError: Int?) {
        Log.d(this@JsonRequestResponseInterface.javaClass.simpleName, networkError.toString())

    }
}