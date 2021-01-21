package net.geeksempire.ready.keep.notes.Utils.Extensions

fun Any?.nullCheckpoint() : String {

    return if (this@nullCheckpoint == null) {
        ""
    } else {
        this@nullCheckpoint.toString()
    }
}

fun Any.print() : Any {

    println("*** " + this@print + " ***")

    return this@print
}