/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 10/18/20 5:18 AM
 * Last modified 10/18/20 5:03 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Calendar

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

fun Any.formatTimestampToMillisecond() : Long {

    return if (this@formatTimestampToMillisecond is Timestamp) {
        (this@formatTimestampToMillisecond as Timestamp).toDate().time
    } else {
        System.currentTimeMillis()
    }
}

fun Timestamp.formatToCurrentTimeZone() : String {

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    return simpleDateFormat.format(this@formatToCurrentTimeZone.toDate())
}

fun String.formatToCurrentTimeZone() : java.sql.Timestamp {

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    val date = simpleDateFormat.parse(this@formatToCurrentTimeZone) as Date

    return java.sql.Timestamp(date.time)
}

fun Long.formatMillisecondCurrentTime(): String {

    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this@formatMillisecondCurrentTime

    return simpleDateFormat.format(calendar.time)
}