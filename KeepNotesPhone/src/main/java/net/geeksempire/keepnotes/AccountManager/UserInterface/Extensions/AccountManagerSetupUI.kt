/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/8/20 9:53 AM
 * Last modified 11/8/20 9:47 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.AccountManager.UserInterface.Extensions

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import net.geeksempire.keepnotes.AccountManager.DataStructure.UserInformationProfileData
import net.geeksempire.keepnotes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.keepnotes.AccountManager.Utils.UserInformation
import net.geeksempire.keepnotes.Preferences.Theme.ThemeType
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.UI.Colors.extractDominantColor
import net.geeksempire.keepnotes.Utils.UI.Colors.extractVibrantColor
import net.geeksempire.keepnotes.Utils.UI.Colors.isColorDark
import net.geeksempire.keepnotes.Utils.UI.Display.statusBarHeight
import java.util.*
import java.util.concurrent.TimeUnit

fun AccountInformation.accountManagerSetupUI() {

    when (themePreferences.checkLightDark()) {
        ThemeType.Light -> {

            accountInformationLayoutBinding.rootView.setBackgroundColor(getColor(R.color.white))

            accountInformationLayoutBinding.profileBlurView.setOverlayColor(getColor(R.color.light_blurry_color))

            accountInformationLayoutBinding.welcomeTextView.setTextColor(getColor(R.color.dark))

            accountInformationLayoutBinding.instagramAddressView.setTextColor(getColor(R.color.dark))
            accountInformationLayoutBinding.instagramAddressLayout.boxBackgroundColor = (getColor(R.color.white))

            accountInformationLayoutBinding.twitterAddressView.setTextColor(getColor(R.color.dark))
            accountInformationLayoutBinding.twitterAddressLayout.boxBackgroundColor = (getColor(R.color.white))

            accountInformationLayoutBinding.phoneNumberAddressView.setTextColor(getColor(R.color.dark))
            accountInformationLayoutBinding.phoneNumberAddressLayout.boxBackgroundColor = (getColor(R.color.white))

        }
        ThemeType.Dark -> {

            accountInformationLayoutBinding.rootView.setBackgroundColor(getColor(R.color.black))

            accountInformationLayoutBinding.profileBlurView.setOverlayColor(getColor(R.color.dark_blurry_color))

            accountInformationLayoutBinding.welcomeTextView.setTextColor(getColor(R.color.light))

            accountInformationLayoutBinding.instagramAddressView.setTextColor(getColor(R.color.light))
            accountInformationLayoutBinding.instagramAddressLayout.boxBackgroundColor = (getColor(R.color.black))

            accountInformationLayoutBinding.twitterAddressView.setTextColor(getColor(R.color.light))
            accountInformationLayoutBinding.twitterAddressLayout.boxBackgroundColor = (getColor(R.color.black))

            accountInformationLayoutBinding.phoneNumberAddressView.setTextColor(getColor(R.color.light))
            accountInformationLayoutBinding.phoneNumberAddressLayout.boxBackgroundColor = (getColor(R.color.black))

        }
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.navigationBarColor = Color.TRANSPARENT
    window.statusBarColor = Color.TRANSPARENT

    accountInformationLayoutBinding.welcomeTextView.text = getString(R.string.welcomeText, if (firebaseAuth.currentUser != null) {
        firebaseAuth.currentUser?.displayName
    } else {
        ""
    })

    var dominantColor = getColor(R.color.yellow)
    var vibrantColor = getColor(R.color.default_color_light)

    window.setBackgroundDrawable(GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, arrayOf(vibrantColor, dominantColor).toIntArray()))

    val accountViewLayoutParameters: ConstraintLayout.LayoutParams = accountInformationLayoutBinding.profileImageView.layoutParams as ConstraintLayout.LayoutParams
    accountViewLayoutParameters.setMargins(accountViewLayoutParameters.topMargin, accountViewLayoutParameters.topMargin + statusBarHeight(applicationContext), accountViewLayoutParameters.topMargin, accountViewLayoutParameters.topMargin)
    accountInformationLayoutBinding.profileImageView.layoutParams = accountViewLayoutParameters

    firebaseAuth.currentUser?.let { firebaseUser ->

        Glide.with(this@accountManagerSetupUI)
            .asDrawable()
            .load(firebaseUser.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(glideException: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {

                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {

                    runOnUiThread {

                        accountInformationLayoutBinding.profileImageView.setImageDrawable(resource)

                        resource?.let {

                            dominantColor = extractDominantColor(applicationContext, it)
                            vibrantColor = extractVibrantColor(applicationContext, it)

                            window.setBackgroundDrawable(GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, arrayOf(vibrantColor, dominantColor).toIntArray()))


                            if (isColorDark(dominantColor) && isColorDark(vibrantColor)) {
                                Log.d(this@accountManagerSetupUI.javaClass.simpleName, "Dark Extracted Colors")

                            } else {
                                Log.d(this@accountManagerSetupUI.javaClass.simpleName, "Light Extracted Colors")

                            }

                        }

                    }

                    return false
                }

            })
            .submit()

        clickSetup()

    }

}

fun AccountInformation.clickSetup() {

    accountInformationLayoutBinding.nextSubmitView.setOnClickListener {

        if (profileUpdating) {

            profileUpdating = false

            this@clickSetup.finish()

        } else {

            profileUpdating = true

            createUserProfile()

        }

    }

}

fun AccountInformation.createUserProfile() {

    firebaseAuth.currentUser?.let { firebaseUser ->

        accountInformationLayoutBinding.updatingLoadingView.visibility = View.VISIBLE
        accountInformationLayoutBinding.updatingLoadingView.playAnimation()

        val userInformationProfileData: UserInformationProfileData = UserInformationProfileData(
            privacyAgreement = userInformationIO.readPrivacyAgreement(),
            userIdentification = firebaseUser.uid,
            userEmailAddress = firebaseUser.email.toString(), userDisplayName = firebaseUser.displayName.toString(), userProfileImage = firebaseUser.photoUrl.toString(),
            instagramAccount = accountInformationLayoutBinding.instagramAddressView.text.toString().toLowerCase(Locale.getDefault()),
            twitterAccount = accountInformationLayoutBinding.twitterAddressView.text.toString(),
            phoneNumber = accountInformationLayoutBinding.phoneNumberAddressView.text.toString(),
        )

        firestoreDatabase
            .document(UserInformation.userProfileDatabasePath(firebaseUser.uid))
            .set(userInformationProfileData)
            .addOnSuccessListener {

                if (!accountInformationLayoutBinding.phoneNumberAddressView.text.isNullOrEmpty()) {

                    val phoneAuthOptions = PhoneAuthOptions.Builder(firebaseAuth).apply {
                        setPhoneNumber(accountInformationLayoutBinding.phoneNumberAddressView.text.toString())
                        setTimeout(120, TimeUnit.SECONDS)
                        setActivity(this@createUserProfile)
                        setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                                Log.d(this@createUserProfile.javaClass.simpleName, "Phone Number Verified")

                                firestoreDatabase
                                    .document(UserInformation.userProfileDatabasePath(firebaseUser.uid))
                                    .update(
                                        "phoneNumberVerified", true,
                                    )

                                firebaseUser.linkWithCredential(phoneAuthCredential).addOnSuccessListener {
                                    Log.d(this@createUserProfile.javaClass.simpleName, "User Profile Linked To Phone Number Authentication")

                                    accountInformationLayoutBinding.updatingLoadingView.pauseAnimation()

                                    accountInformationLayoutBinding.updatingLoadingView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
                                    accountInformationLayoutBinding.updatingLoadingView.visibility = View.INVISIBLE

                                    Handler(Looper.getMainLooper()).postDelayed({

                                        accountInformationLayoutBinding.nextSubmitView.playAnimation()

                                        profileUpdating = true

                                    }, 531)

                                }.addOnFailureListener {
                                    it.printStackTrace()

                                    accountInformationLayoutBinding.updatingLoadingView.pauseAnimation()

                                    accountInformationLayoutBinding.updatingLoadingView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
                                    accountInformationLayoutBinding.updatingLoadingView.visibility = View.INVISIBLE

                                    Handler(Looper.getMainLooper()).postDelayed({

                                        accountInformationLayoutBinding.nextSubmitView.playAnimation()

                                        profileUpdating = true

                                    }, 531)

                                }

                            }

                            override fun onVerificationFailed(e: FirebaseException) {
                                e.printStackTrace()

                                if (e is FirebaseAuthInvalidCredentialsException) {



                                } else if (e is FirebaseTooManyRequestsException) {



                                }

                            }

                            override fun onCodeSent(verificationId: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                                Log.d(this@createUserProfile.javaClass.simpleName, "Verification Code Sent: ${verificationId}")

                            }

                        })
                    }

                    PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions.build())

                } else {

                    accountInformationLayoutBinding.updatingLoadingView.pauseAnimation()

                    accountInformationLayoutBinding.updatingLoadingView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.fade_out))
                    accountInformationLayoutBinding.updatingLoadingView.visibility = View.INVISIBLE

                    Handler(Looper.getMainLooper()).postDelayed({

                        accountInformationLayoutBinding.nextSubmitView.playAnimation()

                        profileUpdating = true

                    }, 531)

                }

            }

    }

}