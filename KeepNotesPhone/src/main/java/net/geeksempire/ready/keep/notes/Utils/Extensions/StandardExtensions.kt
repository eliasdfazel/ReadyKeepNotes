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