package net.geeksempire.ready.keep.notes.Database.NetworkEndpoints

class DatabaseEndpoints {

    fun baseEndpoints (firebaseUserUniqueId: String) : String  {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}"
    }

    fun generalEndpoints (firebaseUserUniqueId: String) : String  {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}/Notes"
    }

    fun handwritingSnapshotEndpoint(firebaseUserUniqueId: String) : String {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}/Notes/HandwritingSnapshot"
    }

    fun voiceRecordingEndpoint(firebaseUserUniqueId: String) : String {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}/Notes/VoiceRecording"
    }

    fun noteTextsEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/TextingNote").plus("/Texts").plus("/Content")
    }

    fun noteTextsDocumentEndpoint(firebaseUserUniqueId: String, noteDocumentId: String) : String {

        return "/ReadyKeepNotes/${firebaseUserUniqueId}/Notes/${noteDocumentId}"
    }

    fun paintPathsDocumentEndpoints (generalEndpoints: String) : String {

        return generalEndpoints.plus("/PaintingNote").plus("/Paths")
    }

    fun paintPathsCollectionEndpoints (documentReference: String) : String {

        return documentReference.plus("/PaintingNote").plus("/Paths").plus("/Content")
    }

}