package net.geeksempire.keepnotes.Database.GeneralEndpoints

class DatabaseEndpoints() {

    fun GeneralEndpoints (firebaseUserUniqueId: String) : String  {

        return "/KeepNotes/${firebaseUserUniqueId}/Notes"
    }

}