/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/8/20 9:53 AM
 * Last modified 11/8/20 9:53 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.AccountManager.UserInterface

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnotes.AccountManager.DataStructure.UserInformationDataStructure
import net.geeksempire.keepnotes.AccountManager.UserInterface.Extensions.accountManagerSetupUI
import net.geeksempire.keepnotes.AccountManager.UserInterface.Extensions.createUserProfile
import net.geeksempire.keepnotes.AccountManager.Utils.UserInformation
import net.geeksempire.keepnotes.AccountManager.Utils.UserInformationIO
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.Network.NetworkCheckpoint
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListener
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.keepnotes.databinding.AccountInformationLayoutBinding
import java.util.*
import javax.inject.Inject

class AccountInformation : AppCompatActivity(), NetworkConnectionListenerInterface {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val userInformation: UserInformation by lazy {
        UserInformation(this@AccountInformation)
    }

    val userInformationIO: UserInformationIO by lazy {
        UserInformationIO(applicationContext)
    }

    val firebaseAuthentication = Firebase.auth

    val firestoreDatabase: FirebaseFirestore = Firebase.firestore

    var profileUpdating: Boolean = false

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var accountInformationLayoutBinding: AccountInformationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountInformationLayoutBinding = AccountInformationLayoutBinding.inflate(layoutInflater)
        setContentView(accountInformationLayoutBinding.root)

        accountManagerSetupUI()

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@AccountInformation, accountInformationLayoutBinding.rootView)
            .inject(this@AccountInformation)

        networkConnectionListener.networkConnectionListenerInterface = this@AccountInformation

        if (firebaseAuthentication.currentUser == null) {

            accountInformationLayoutBinding.signupLoadingView.visibility = View.VISIBLE
            accountInformationLayoutBinding.signupLoadingView.playAnimation()

            userInformation.startSignInProcess()

        } else {

            firebaseAuthentication.currentUser?.let { firebaseUser ->

                firestoreDatabase
                    .document(UserInformation.userProfileDatabasePath(firebaseUser.uid))
                    .get()
                    .addOnSuccessListener { documentSnapshot ->

                        documentSnapshot?.let { documentData ->

                            accountInformationLayoutBinding.socialMediaScrollView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                            accountInformationLayoutBinding.socialMediaScrollView.visibility = View.VISIBLE

                            accountInformationLayoutBinding.instagramAddressView.setText(documentData.data?.get(
                                UserInformationDataStructure.instagramAccount).toString().toLowerCase(Locale.getDefault()))

                            accountInformationLayoutBinding.twitterAddressView.setText(documentData.data?.get(UserInformationDataStructure.twitterAccount).toString())

                            accountInformationLayoutBinding.phoneNumberAddressView.setText(documentData.data?.get(UserInformationDataStructure.phoneNumber).toString())

                            accountInformationLayoutBinding.inviteDirectlyPrivateMessage.visibility = View.VISIBLE
                            accountInformationLayoutBinding.inviteDirectlyPrivateMessage.setOnClickListener {

                                //

                            }

                        }

                    }

            }

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let {

            when (requestCode) {
                UserInformation.GoogleSignInRequestCode -> {

                    val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                    googleSignInAccountTask.addOnSuccessListener {

                        val googleSignInAccount = googleSignInAccountTask.getResult(ApiException::class.java)

                        val authCredential = GoogleAuthProvider.getCredential(googleSignInAccount?.idToken, null)
                        firebaseAuthentication.signInWithCredential(authCredential).addOnSuccessListener {

                            val firebaseUser = firebaseAuthentication.currentUser

                            if (firebaseUser != null) {

                                val accountName: String = firebaseUser.email.toString()

                                userInformationIO.saveUserInformation(accountName)

                                createUserProfile()

                            }

                        }.addOnFailureListener {


                        }

                    }.addOnFailureListener {
                        it.printStackTrace()

                    }

                }
                else -> {

                }
            }

        }

    }

    override fun onBackPressed() {

        if (profileUpdating) {

            profileUpdating = false

            this@AccountInformation.finish()

        } else {

            this@AccountInformation.finish()

        }

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    }

    override fun networkAvailable() {

    }

    override fun networkLost() {

    }

}