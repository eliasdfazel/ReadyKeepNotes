package net.geeksempire.ready.keep.notes

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import net.geeksempire.ready.keep.notes.AccountManager.SignInProcess.SetupAccount
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformationIO
import net.geeksempire.ready.keep.notes.Browser.BuiltInBrowser
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.EntryConfigurationLayoutBinding
import javax.inject.Inject

class EntryConfigurations : AppCompatActivity(), NetworkConnectionListenerInterface {

    private val userInformationIO: UserInformationIO by lazy {
        UserInformationIO(applicationContext)
    }

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    companion object {
        const val PermissionRequestCode: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@EntryConfigurations, entryConfigurationLayoutBinding.rootView)
            .inject(this@EntryConfigurations)

        networkConnectionListener.networkConnectionListenerInterface = this@EntryConfigurations

        if (userInformationIO.readPrivacyAgreement()) {

            entryConfigurationLayoutBinding.agreementDataView.visibility = View.INVISIBLE
            entryConfigurationLayoutBinding.proceedButton.visibility = View.INVISIBLE

            runtimePermission()

        } else {

            entryConfigurationLayoutBinding.agreementDataView.visibility = View.VISIBLE
            entryConfigurationLayoutBinding.proceedButton.visibility = View.VISIBLE

            entryConfigurationLayoutBinding.rootView.post {

                entryConfigurationLayoutBinding.blurryBackground.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

            }

            entryConfigurationLayoutBinding.proceedButton.setOnClickListener {

                entryConfigurationLayoutBinding.agreementDataView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                entryConfigurationLayoutBinding.agreementDataView.visibility = View.INVISIBLE

                entryConfigurationLayoutBinding.proceedButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                entryConfigurationLayoutBinding.proceedButton.visibility = View.INVISIBLE

                runtimePermission()

            }

            entryConfigurationLayoutBinding.agreementDataView.setOnClickListener {

                BuiltInBrowser.show(
                    context = applicationContext,
                    linkToLoad = getString(R.string.privacyAgreementLink),
                    gradientColorOne = getColor(R.color.default_color_dark),
                    gradientColorTwo = getColor(R.color.default_color_game_dark)
                )

            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults)

        when (requestCode) {
            EntryConfigurations.PermissionRequestCode -> {

                if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {

                    val setupAccount = SetupAccount()

                    if (setupAccount.firebaseUser == null) {

                        entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

                        entryConfigurationLayoutBinding.waitingView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        entryConfigurationLayoutBinding.waitingView.visibility = View.VISIBLE

                        entryConfigurationLayoutBinding.waitingInformationView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        entryConfigurationLayoutBinding.waitingInformationView.visibility = View.VISIBLE

                        val signInAnonymously = {
                            setupAccount.signInAnonymously()
                        }

                        signInAnonymously.invoke().addOnSuccessListener {

                            entryConfigurationLayoutBinding.waitingView.visibility = View.GONE
                            entryConfigurationLayoutBinding.waitingInformationView.visibility = View.GONE

                            openTakeNoteActivity()

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

                        openOverviewActivity()

                    }

                } else {

                    runtimePermissionMessage()

                }

            }
        }

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

    private fun openOverviewActivity() {

        startActivity(Intent(applicationContext, KeepNoteOverview::class.java).apply {

        }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

        this@EntryConfigurations.finish()

    }

    private fun openTakeNoteActivity() {

        startActivity(Intent(applicationContext, TakeNote::class.java).apply {
            putExtra("IncomingActivityName", EntryConfigurations::class.java.simpleName)
        }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

        this@EntryConfigurations.finish()

    }

    private fun runtimePermission() {

        userInformationIO.savePrivacyAgreement()

        val permissionsList = arrayListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE
        )

        requestPermissions(
            permissionsList.toTypedArray(),
            EntryConfigurations.PermissionRequestCode
        )

    }

    private fun runtimePermissionMessage() {

        SnackbarBuilder(applicationContext).show (
            rootView = entryConfigurationLayoutBinding.rootView,
            messageText= getString(R.string.permissionMessage),
            messageDuration = Snackbar.LENGTH_INDEFINITE,
            actionButtonText = R.string.grantPermission,
            snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                override fun onActionButtonClicked(snackbar: Snackbar) {
                    super.onActionButtonClicked(snackbar)

                    runtimePermission()

                }

            }
        )

    }

}