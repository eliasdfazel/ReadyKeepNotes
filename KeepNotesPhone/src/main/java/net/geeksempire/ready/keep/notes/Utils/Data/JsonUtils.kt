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

import org.json.JSONArray

fun JSONArray.forEach() {



}
fun JSONArray.forEach(action: (Any) -> Unit): Unit {
    for (element in 0 until this.length()) action(this.get(element))
}