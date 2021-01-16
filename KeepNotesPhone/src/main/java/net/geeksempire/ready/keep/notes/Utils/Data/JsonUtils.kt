package net.geeksempire.ready.keep.notes.Utils.Data

import org.json.JSONArray

fun JSONArray.forEach() {



}
fun JSONArray.forEach(action: (Any) -> Unit): Unit {
    for (element in 0 until this.length()) action(this.get(element))
}