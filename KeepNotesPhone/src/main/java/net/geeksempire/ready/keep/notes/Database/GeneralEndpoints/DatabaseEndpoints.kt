package net.geeksempire.ready.keep.notes.Database.GeneralEndpoints

class DatabaseEndpoints {

    fun generalEndpoints (firebaseUserUniqueId: String) : String  {

        return "/KeepNotes/${firebaseUserUniqueId}/Notes"
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