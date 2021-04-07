package net.geeksempire.ready.keep.notes.SecurityConfiguratios.Biometric

import android.app.ActivityOptions
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R

class BiometricAuthentication (private val activity: AppCompatActivity) {

    fun startAuthenticationProcess() {

        val biometricManager = BiometricManager.from(activity)

        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {

            val authenticationExecutor = ContextCompat.getMainExecutor(activity)

            val biometricPrompt: BiometricPrompt = BiometricPrompt(activity, authenticationExecutor, object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(authenticationResult: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(authenticationResult)

                    activity.startActivity(Intent(activity, KeepNoteOverview::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }, ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, android.R.anim.fade_out).toBundle())

                    activity.finish()

                    Log.d(this@BiometricAuthentication.javaClass.simpleName, "Authentication Succeeded")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()



                    Log.d(this@BiometricAuthentication.javaClass.simpleName, "Authentication Failed")
                }

                override fun onAuthenticationError(errorCode: Int, errorString: CharSequence) {
                    super.onAuthenticationError(errorCode, errorString)



                    Log.d(this@BiometricAuthentication.javaClass.simpleName, "Authentication Error")
                }

            })

            biometricPrompt.authenticate(
                BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.biometricTitle))
                .setDescription(activity.getString(R.string.biometricDescription))
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build())

        } else {



        }

    }

}