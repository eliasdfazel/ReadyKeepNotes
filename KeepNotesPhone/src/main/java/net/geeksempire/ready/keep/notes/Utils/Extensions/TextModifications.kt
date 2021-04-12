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

import androidx.annotation.Keep

fun String.capitalizeFirstChar(): String {

    if (this@capitalizeFirstChar.isEmpty()) {
        return ""
    }

    val first = this@capitalizeFirstChar[0]

    return if (Character.isUpperCase(first)) {

        this@capitalizeFirstChar

    } else {

        Character.toUpperCase(first).toString() + this@capitalizeFirstChar.substring(1)
    }

}

/**
 *
 *  Special Characters  * # . - ☑ ✅
 *
 **/
@Keep
data class SpecialCharacterData(var detected: Boolean, var specialCharacter: String?)

/**
 *
 *  Special Characters  * # . - ☑ ✅
 *
 **/
fun String.checkSpecialCharacters() : SpecialCharacterData {

    val arrayOfSpecialCharacters = arrayListOf<String>("*", "#", ".", "-", "☑", "✅")

    arrayOfSpecialCharacters.forEachIndexed { index, detectedSpecialCharater ->

        if (this@checkSpecialCharacters == detectedSpecialCharater) {

            return SpecialCharacterData(true, "${detectedSpecialCharater} ")
        }

    }

    return SpecialCharacterData(false, null)
}

