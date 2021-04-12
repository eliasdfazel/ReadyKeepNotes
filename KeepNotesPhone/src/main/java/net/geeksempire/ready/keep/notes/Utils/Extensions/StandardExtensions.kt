/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Extensions

fun Any?.nullCheckpointString() : String {

    return if (this@nullCheckpointString == null) {
        ""
    } else {
        this@nullCheckpointString.toString()
    }
}

fun Any?.nullCheckpointInteger() : Int? {

    return if (this@nullCheckpointInteger == null) {
        null
    } else {
        this@nullCheckpointInteger.toString().toInt()
    }
}

fun Any.println() : Any {

    println("*** " + this@println + " ***")

    return this@println
}