package net.geeksempire.ready.keep.notes.Database.NetworkEndpoints

class DatabaseEndpoints {

    fun generalEndpoints (firebaseUserUniqueId: String) : String  {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}/Notes"
    }

    fun noteTextsEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/TextingNote").plus("/Texts").plus("/Content")
    }

    fun paintPathsDocumentEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/PaintingNote").plus("/Paths")
    }

    fun paintPathsCollectionEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/PaintingNote").plus("/Paths").plus("/Content")
    }

}