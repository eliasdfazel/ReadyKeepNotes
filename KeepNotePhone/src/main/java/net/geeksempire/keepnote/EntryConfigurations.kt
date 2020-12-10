package net.geeksempire.keepnote

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import net.geeksempire.keepnote.AccountManager.SignInProcess.SetupAccount
import net.geeksempire.keepnote.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.keepnote.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.keepnote.databinding.EntryConfigurationLayoutBinding

class EntryConfigurations : AppCompatActivity() {

    private val setupAccount = SetupAccount()

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)

        if (setupAccount.firebaseUser == null) {

            entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

            entryConfigurationLayoutBinding.waitingView.visibility = View.VISIBLE
            entryConfigurationLayoutBinding.waitingInformationView.visibility = View.VISIBLE

            val signInAnonymously = {
                setupAccount.signInAnonymously()
            }

            signInAnonymously.invoke().addOnSuccessListener {

                entryConfigurationLayoutBinding.waitingView.visibility = View.GONE
                entryConfigurationLayoutBinding.waitingInformationView.visibility = View.GONE



            }.addOnFailureListener {

                SnackbarBuilder(applicationContext).show (
                    rootView = entryConfigurationLayoutBinding.rootView,
                    messageText= getString(R.string.anonymouslySignInError),
                    messageDuration = Snackbar.LENGTH_INDEFINITE,
                    actionButtonText = R.string.retryText,
                    snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                        override fun onActionButtonClicked(snackbar: Snackbar) {
                            super.onActionButtonClicked(snackbar)

                            signInAnonymously.invoke()

                        }

                    }
                )

            }

        } else {



        }

    }


}