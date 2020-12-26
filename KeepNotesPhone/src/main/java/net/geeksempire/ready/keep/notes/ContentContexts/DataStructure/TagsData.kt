package net.geeksempire.ready.keep.notes.ContentContexts.DataStructure

object TextRazorParameters {
    const val Response = "response"
    const val ExtractorsEntities = "entities"
    const val AnalyzedText = "matchedText"
    const val WikiLink = "wikiLink"
    const val WikiDataId = "wikidataId"
}

data class TagsData (var wikiDataId: String, var wikiLink: String, var aTag: String)