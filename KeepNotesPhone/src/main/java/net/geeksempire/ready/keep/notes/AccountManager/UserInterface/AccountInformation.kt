/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/8/20 9:53 AM
 * Last modified 11/8/20 9:53 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.AccountManager.UserInterface

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.DataStructure.UserInformationDataStructure
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.Extensions.accountManagerSetupUserInterface
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.Extensions.createUserProfile
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformation
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformationIO
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.Invitations.Send.SendInvitation
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.AccountInformationLayoutBinding
import java.io.File
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

    var firebaseAuthentication = Firebase.auth

    val firebaseUser = firebaseAuthentication.currentUser

    var profileUpdating: Boolean = false

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var accountInformationLayoutBinding: AccountInformationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountInformationLayoutBinding = AccountInformationLayoutBinding.inflate(layoutInflater)
        setContentView(accountInformationLayoutBinding.root)

        accountManagerSetupUserInterface()

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@AccountInformation, accountInformationLayoutBinding.rootView)
            .inject(this@AccountInformation)

        networkConnectionListener.networkConnectionListenerInterface = this@AccountInformation

        if (firebaseUser != null
            && firebaseUser.isAnonymous) {

            accountInformationLayoutBinding.signupLoadingView.visibility = View.VISIBLE
            accountInformationLayoutBinding.signupLoadingView.playAnimation()

            accountInformationLayoutBinding.root.post {

                Handler(Looper.getMainLooper()).postDelayed({

                    userInformation.startSignInProcess()

                }, 531)

            }

        } else if (firebaseUser != null) {

            (application as KeepNoteApplication).firestoreDatabase
                .document(UserInformation.userProfileDatabasePath(firebaseUser.uid))
                .get()
                .addOnSuccessListener { documentSnapshot ->

                    documentSnapshot?.let { documentData ->

                        accountInformationLayoutBinding.socialMediaScrollView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_in))
                        accountInformationLayoutBinding.socialMediaScrollView.visibility = View.VISIBLE

                        accountInformationLayoutBinding.instagramAddressView.setText(documentData.data?.get(
                            UserInformationDataStructure.instagramAccount).toString().toLowerCase(Locale.getDefault()))

                        accountInformationLayoutBinding.twitterAddressView.setText(documentData.data?.get(
                            UserInformationDataStructure.twitterAccount).toString())

                        accountInformationLayoutBinding.phoneNumberAddressView.setText(documentData.data?.get(
                            UserInformationDataStructure.phoneNumber).toString())

                        accountInformationLayoutBinding.inviteFriendsView.visibility = View.VISIBLE
                        accountInformationLayoutBinding.inviteFriendsView.setOnClickListener {

                            if (checkSelfPermission(Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {

                                SendInvitation(applicationContext, accountInformationLayoutBinding.root)
                                    .invite(firebaseUser)

                            } else {

                                val permissionsList = arrayListOf(
                                    Manifest.permission.GET_ACCOUNTS
                                )

                                requestPermissions(
                                    permissionsList.toTypedArray(),
                                    EntryConfigurations.PermissionRequestCode
                                )

                            }

                        }

                    }

                }

        }

        firebaseAuthentication.addAuthStateListener { authentication ->

            if (authentication.currentUser == null) {
                Log.d(this@AccountInformation.javaClass.simpleName, "Firebase Authenticator Couldn't Find Firebase User.")

                firebaseAuthentication.signOut().also {
                    Log.d(this@AccountInformation.javaClass.simpleName, "Firebase User Information Delete Locally.")

                    try {
                        File("/data/data/${packageName}/").delete()
                    } catch (e: Exception){
                        e.printStackTrace()
                    }

                    userInformation.startSignInProcess()

                }

            }

        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        firebaseUser?.reload()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        data?.let {

            when (requestCode) {
                UserInformation.GoogleSignInRequestCode -> {

                    val googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data)
                    googleSignInAccountTask.addOnSuccessListener {

                        val googleSignInAccount = googleSignInAccountTask.getResult(ApiException::class.java)

                        val emailAddress = googleSignInAccount.email

                        if (emailAddress != null) {

                            firebaseAuthentication.fetchSignInMethodsForEmail(emailAddress)
                                .addOnSuccessListener { signInMethodQuery ->
                                    Log.d(this@AccountInformation.javaClass.simpleName, "Current Sign In Methods: ${signInMethodQuery.signInMethods.toString()}")

                                    val authenticationCredential = GoogleAuthProvider.getCredential(googleSignInAccount?.idToken, null)

                                    firebaseUser?.linkWithCredential(authenticationCredential)?.addOnSuccessListener {
                                        Log.d(this@AccountInformation.javaClass.simpleName, "Anonymous Credential Linked With Google Account Credential.")

                                        val userProfileChangeRequestBuilder = UserProfileChangeRequest.Builder().apply {
                                            displayName = googleSignInAccount.displayName
                                            photoUri = googleSignInAccount.photoUrl
                                        }

                                        firebaseUser.updateProfile(userProfileChangeRequestBuilder.build())
                                            .addOnSuccessListener {
                                                Log.d(this@AccountInformation.javaClass.simpleName, "Firebase User Profile Updated.")

                                                val accountName: String = firebaseAuthentication.currentUser?.email.toString()

                                                userInformationIO.saveUserInformation(accountName)

                                                createUserProfile()

                                            }

                                    }?.addOnFailureListener {
                                        it.printStackTrace()

                                        when (it) {
                                            is FirebaseAuthUserCollisionException -> {
                                                Log.w(this@AccountInformation.javaClass.simpleName, it.message.orEmpty())

                                                firebaseUser.delete().addOnSuccessListener {

                                                    firebaseAuthentication.signInWithCredential(authenticationCredential)
                                                        .addOnSuccessListener { authResult ->

                                                            val accountName: String = authResult.user?.email.toString()

                                                            userInformationIO.saveUserInformation(accountName)

                                                            userInformationIO.saveNewUserInformation(authResult.additionalUserInfo?.isNewUser?:false)

                                                            createUserProfile()

                                                        }.addOnFailureListener {



                                                        }

                                                }

                                            }
                                        }

                                    }

                                }.addOnFailureListener {
                                    it.printStackTrace()
                                }

                        } else {

                            Toast.makeText(applicationContext, getString(R.string.waitingInformation), Toast.LENGTH_LONG).show()

                            Handler(Looper.getMainLooper()).postDelayed({

                                userInformation.startSignInProcess()

                            }, 777)

                        }

                    }

                }
                else -> {

                }
            }

        }

    }

    override fun onBackPressed() {

        if (firebaseAuthentication.currentUser == null) {

            SnackbarBuilder(applicationContext).show(
                rootView = accountInformationLayoutBinding.rootView,
                messageText = getString(R.string.anonymouslySignInError),
                messageDuration = Snackbar.LENGTH_INDEFINITE,
                actionButtonText = R.string.signInText,
                snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                    override fun onActionButtonClicked(snackbar: Snackbar) {
                        super.onActionButtonClicked(snackbar)

                        userInformation.startSignInProcess()

                    }

                }
            )

        } else {

            if (profileUpdating) {

                profileUpdating = false

                this@AccountInformation.finish()

            } else {

                this@AccountInformation.finish()

            }

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

    }

    override fun networkAvailable() {

    }

    override fun networkLost() {

    }

}