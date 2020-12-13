package net.geeksempire.keepnote.Database.GeneralEndpoints

class DatabaseEndpoints() {

    fun GeneralEndpoints (firebaseUserUniqueId: String) : String  {

        return "/KeepNote/${firebaseUserUniqueId}/Notes"
    }

}