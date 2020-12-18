/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 10/18/20 9:04 AM
 * Last modified 10/18/20 9:04 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.AccountManager.Utils

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import net.geeksempire.keepnotes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.keepnotes.R

class UserInformation(private val context: AccountInformation) {

    companion object {
        const val GoogleSignInRequestCode = 103

        fun userProfileDatabasePath(userUniqueIdentifier: String) : String = "Vicinity/UserInformation/${userUniqueIdentifier}/Profile"

    }

    fun startSignInProcess() {

        if (context.firebaseAuthentication.currentUser == null) {

            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.webClientId))
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
            googleSignInClient.signInIntent.run {
                context.startActivityForResult(this@run, GoogleSignInRequestCode)
            }

        }

    }

}