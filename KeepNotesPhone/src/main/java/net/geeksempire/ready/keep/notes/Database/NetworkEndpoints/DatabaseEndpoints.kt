package net.geeksempire.ready.keep.notes.Database.NetworkEndpoints

class DatabaseEndpoints {

    fun userDatabaseEndpoints (firebaseUserUniqueId: String) : String  {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}"
    }

    /**
     * All Notes = /ReadyKeepNotes/FirebaseUserUniqueId/Notes
     **/
    fun generalEndpoints (firebaseUserUniqueId: String) : String  {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes"
    }

    fun baseSpecificNoteEndpoint(firebaseUserUniqueId: String, uniqueDocumentId: String) : String {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes" +
                "/" +
                "${uniqueDocumentId}" +
                "/"
    }

    fun handwritingSnapshotEndpoint(firebaseUserUniqueId: String, uniqueDocumentId: String) : String {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes" +
                "/" +
                "${uniqueDocumentId}" +
                "/" +
                "HandwritingSnapshot"
    }

    fun voiceRecordingEndpoint(firebaseUserUniqueId: String, uniqueDocumentId: String) : String {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes" +
                "/" +
                "${uniqueDocumentId}" +
                "/" +
                "VoiceRecording"
    }

    fun imageEndpoint(firebaseUserUniqueId: String, uniqueDocumentId: String) : String {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes" +
                "/" +
                "${uniqueDocumentId}" +
                "/" +
                "Image"
    }

    fun noteTextsEndpoints (generalEndpoints: String) : String {

        return generalEndpoints
            .plus("/TextingNote")
            .plus("/Texts")
            .plus("/Content")
    }

    fun noteTextsDocumentEndpoint(firebaseUserUniqueId: String, noteDocumentId: String) : String {

        return "/" +
                "ReadyKeepNotes" +
                "/" +
                "${firebaseUserUniqueId}" +
                "/" +
                "Notes" +
                "/" +
                "${noteDocumentId}"
    }

    fun paintPathsDocumentEndpoints (generalEndpoints: String) : String {

        return generalEndpoints
            .plus("/PaintingNote")
            .plus("/Paths")
    }

    fun paintPathsCollectionEndpoints (documentReference: String) : String {

        return documentReference
            .plus("/PaintingNote")
            .plus("/Paths")
            .plus("/Content")
    }

}