/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.ContentContexts.DataStructure

import androidx.annotation.Keep

object TextRazorParameters {
    const val Response = "response"
    const val ExtractorsEntities = "entities"
    const val AnalyzedText = "matchedText"
    const val WikiLink = "wikiLink"
    const val WikiDataId = "wikidataId"
}

@Keep
data class TagsData (var wikiDataId: String, var wikiLink: String, var aTag: String)