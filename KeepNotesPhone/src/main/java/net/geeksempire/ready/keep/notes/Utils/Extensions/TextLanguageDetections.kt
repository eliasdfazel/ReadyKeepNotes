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