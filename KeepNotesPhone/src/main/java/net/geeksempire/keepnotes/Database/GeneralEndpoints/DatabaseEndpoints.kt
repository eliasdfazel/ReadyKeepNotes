package net.geeksempire.keepnotes.Database.GeneralEndpoints

class DatabaseEndpoints {

    fun GeneralEndpoints (firebaseUserUniqueId: String) : String  {

        return "/KeepNotes/${firebaseUserUniqueId}/Notes"
    }

    fun NoteTextsEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/TextingNote").plus("/Texts").plus("/Content")
    }

    fun PaintPathsEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/PaintingNote").plus("/Paths").plus("/Content")
    }

}