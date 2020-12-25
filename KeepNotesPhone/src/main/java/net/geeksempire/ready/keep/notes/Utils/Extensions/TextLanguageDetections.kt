/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 9/23/20 10:40 AM
 * Last modified 9/23/20 10:00 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.Extensions

class TextLanguageDetections {

    /**
     * RTL Language: True
     **/
    fun checkRtl(aString: String): Boolean {

        return if (aString.isNotBlank() && aString.isNotEmpty()) {

            val firstChar = aString[0]

            (firstChar.toInt() in 0x590..0x6ff)
        } else {

             (false)
        }
    }

}