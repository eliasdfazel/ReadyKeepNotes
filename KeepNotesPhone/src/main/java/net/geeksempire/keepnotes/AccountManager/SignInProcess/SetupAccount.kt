package net.geeksempire.keepnotes.AccountManager.SignInProcess

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SetupAccount {

    val firebaseAuth: FirebaseAuth = Firebase.auth
    val firebaseUser = firebaseAuth.currentUser

    fun signInAnonymously() : Task<AuthResult>  {

        return firebaseAuth.signInAnonymously()
    }

}